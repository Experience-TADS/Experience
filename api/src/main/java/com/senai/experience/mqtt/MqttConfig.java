package com.senai.experience.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Configuração do consumidor MQTT via Spring Integration.
 *
 * <ul>
 *   <li>Conecta ao broker Mosquitto (URL configurável por variável de ambiente
 *       {@code MQTT_BROKER_URL}; padrão {@code tcp://localhost:1883} para dev local,
 *       {@code tcp://mqtt:1883} no Docker via variável injetada pelo compose).</li>
 *   <li>Assina o tópico {@code senai/pii_toy} com QoS 2.</li>
 *   <li>Reconecta automaticamente em caso de queda (keepAlive + connectionTimeout).</li>
 * </ul>
 */
@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topic.esp32}")
    private String topic;

    // ── Factory ───────────────────────────────────────────────────────────────

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        // Reconecta automaticamente sem intervenção manual
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);   // segundos
        options.setKeepAliveInterval(30);   // segundos

        factory.setConnectionOptions(options);
        return factory;
    }

    // ── Canal de entrada ──────────────────────────────────────────────────────

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    // ── Adaptador de entrada (subscriber) ────────────────────────────────────

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory(), topic);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    // ── Handler delegado ao listener de negócio ───────────────────────────────

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler(MqttEspEventoListener listener) {
        return listener;
    }
}

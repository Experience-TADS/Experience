package com.senai.experience.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

/**
 * Deserializer global de String — S-011.
 *
 * Toda String recebida em requisições JSON ({@code @RequestBody}) passa por este
 * deserializer, que escapa apenas os caracteres perigosos para XSS
 * ({@code & < > " '}) convertendo-os em suas entidades HTML. Com isso, payloads
 * como {@code <script>} são neutralizados antes de chegar à camada de serviço ou
 * ao banco, enquanto acentos e demais caracteres Unicode legítimos (ex.: "João")
 * são preservados intactos — ao contrário de {@code HtmlUtils.htmlEscape}, que
 * converteria "ã" em "&atilde;".
 *
 * <p>A proteção contra SQL injection é garantida separadamente pelo uso exclusivo de
 * queries parametrizadas (JPA/JPQL), sem concatenação de string.</p>
 */
public class SanitizingStringDeserializer extends StdScalarDeserializer<String> {

    public SanitizingStringDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String valor = p.getValueAsString();
        if (valor == null) {
            return null;
        }
        return escaparXss(valor.trim());
    }

    /**
     * Escapa somente os cinco caracteres relevantes para XSS em contexto HTML,
     * preservando acentuação e demais caracteres. A ordem importa: {@code &}
     * precisa ser escapado primeiro para não re-escapar as entidades geradas.
     */
    private static String escaparXss(String input) {
        if (input.isEmpty()) {
            return input;
        }
        StringBuilder sb = new StringBuilder(input.length() + 16);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&'  -> sb.append("&amp;");
                case '<'  -> sb.append("&lt;");
                case '>'  -> sb.append("&gt;");
                case '"'  -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default   -> sb.append(c);
            }
        }
        return sb.toString();
    }
}

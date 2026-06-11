FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY simulador.py .

# Mantém o container vivo sem executar nada
# Para rodar: docker exec -it experience-simulador python simulador.py
CMD ["tail", "-f", "/dev/null"]
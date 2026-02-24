from gradle:8.14.4-jdk21
label authors="ffazio"

##aqui pode-se embutir as variaveis de ambiente do projeto para a nossa imagem
#caso tenha no projeto como por exemplo .env.dev
#ARG ENV_FILE=.env-dev
# Copia o arquivo para dentro da imagem para uso
#COPY ${ENV_FILE} .env
# e se eu quisesse rodar de acordo com o ambiente
# só rodar no terminal docker build --build-arg ENV_FILE=.env.prod -t minha-app .
#ou rodar o comando do compose pra dev ou prod ja deve resolver se  eu tiver um args COM ENV_FILE=.env desejada
run mkdir /app
#adiciona os arquivos do pc local (seu pc) para o local do projeto no container
#copying the files from local system to container
add . /app/embalagenspamplona

#diretório onde será executado o app
workdir /app/embalagenspamplona

#acessando o diretório e limpando ele
run rm -rf /app/embagenspamplona/build
run ./gradlew clean
copy ./build /app/embalagenspamplona/build
#PRA REFERENCIAR O ARQUIVO JAR SEM NOME É USAR *.JAR
run ./gradlew build --no-daemon -x test

env API_PORT=9080
env MYSQL_PORT=9954
env REDIS_PORT=6379
env SQL_ADDRESS=127.0.0.1
env DB_NAME=embalagens_pamplona
env JWT_KEY=hauhauhauhauhauhsudhusahuhsaudsa

#roda o comando depois que o container terminar de inicializar
cmd ["java", "-jar", "/app/embalagenspamplona/build/libs/embalagenspamplona-0.0.1-SNAPSHOT.jar"]
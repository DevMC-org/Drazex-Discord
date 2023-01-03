FROM openjdk:18-alpine

RUN apk update && apk upgrade && apk add bash

WORKDIR /devmc

ENV HOST 0.0.0.0

EXPOSE 6503

ENTRYPOINT ["bots/discord/entrypoint.sh"]

COPY /build/libs/DrazexBot-discord.jar bots/discord/
COPY entrypoint.sh bots/discord/

RUN chmod +x bots/discord/entrypoint.sh
# Banana Car Notification Service

**Tired of frantically refreshing Banana Car to find that ride tomorrow? Fear not!**

This application automates it with Discord integration:

![discord_banana_car.png](pictures/discord_banana_car.png)

## Discord commands

There are several commands available to manage subscriptions. Typing any of them in Discord wil provide more
information:

* `/rides subscriptions list`
* `/rides subscriptions create`
* `/rides subscriptions delete`

## Set up

1. Make sure to set up properties

```properties
discord.channelId=replace-secret
discord.token=replace-secret
```

2. Start database `docker compose up -d`
3. Run the application

## Schema changes

1. Add new database migration in `src/main/resources/db/changelog`
2. Start application
3. Run `./gradlew :generateJooq`

Database classes should now appear in `src/main/generated-db-entities`
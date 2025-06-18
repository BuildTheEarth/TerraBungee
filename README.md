<!-- markdownlint-disable -->
<div align="center">

<img width="128" src="https://github.com/BuildTheEarth/assets/blob/main/images/logos/logo_archive_2.png?raw=true" />

# TerraBungee

_A system that allows for dynamic BungeeCord backend server management._

![official](https://go.buildtheearth.net/official-shield)
[![chat](https://img.shields.io/discord/706317564904472627.svg?color=768AD4&label=discord&logo=https%3A%2F%2Fdiscordapp.com%2Fassets%2F8c9701b98ad4372b58f13fd9f65f966e.svg)](https://discord.gg/buildtheearth)

</div>
<!-- markdownlint-restore -->

Created by [Saghetti#7380](https://github.com/Saghetti0). Developed by [noahhusby#6512](https://github.com/noahhusby).

## Features
* Multi-Proxy Binding - Syncs server data between multiple bungeecord proxies.
* Instance Deployment - Instantly deploy individual minecraft server instances and sync them with the proxies.
* Fleet Scaling - Automatically scale the instances up/down based upon network traffic and API calls.
* Load Balancing - Automatically chooses the best server for a player to join.

## Modules

This repository contains the following modules:

| Name                       | Description                          |
| -------------------------- | ------------------------------------ |
| [api](https://github.com/BuildTheEarth/TerraBungee/tree/master/api)                        | API for all TerraBungee services. This can be used to create custom services (Minigame Libraries, Discord Bots, etc.)     |
| [controller](https://github.com/BuildTheEarth/TerraBungee/tree/master/controller)                 | Controller for TerraBungee. Connects all services together and performs logic for load balencing and fleet scaling |
| [proxy](https://github.com/BuildTheEarth/TerraBungee/tree/master/proxy)                      | Bungeecord plugin for TerraBungee. Keeps track of player data/locations. Performs the actual load balancing of players. Adds/deletes instances automatically when added to the controller  |
| [instance](https://github.com/BuildTheEarth/TerraBungee/tree/master/instance)                   | Bukkit plugin for TerraBungee. Keeps track of individual instances. Does other cool magic things in the background |

## Installation
Go to the [releases](https://github.com/BuildTheEarth/TerraBungee/releases) page and download the latest bungeecord/bukkit plugin, node, and controller.

## Configuration

### Default Controller Configuration:
```yaml
# Configuration file

##########################################################################################################
# discord
#--------------------------------------------------------------------------------------------------------#
# Settings for the discord bot
##########################################################################################################

discord {
    # The token of the discord bot. [default: ]
    S:"Bot Token"=

    # The ID of the Discord Guild/Server that the bot should listen on. [default: ]
    S:"Guild ID"=

    # The ID of the Discord channel where TerraBungee updates should be sent. [default: ]
    S:"Updates Channel ID"=
}


##########################################################################################################
# general
#--------------------------------------------------------------------------------------------------------#
# General settings for the TerraBungee Controller
##########################################################################################################

general {
    # The IP address that the controller should run on. [default: 127.0.0.1]
    S:Host=127.0.0.1

    # The port that the controller should run on. [range: 0 ~ 65535, default: 7000]
    I:Port=7000
}
```

### Default Bungeecord Configuration:
```yaml
# TerraBungee
# (C) 2020 noahhusby

# BungeeCord Config

# The URL that the controller can be accessed from
# NOTE: must end with a slash
# It is recommended to use 127.0.0.1 instead of localhost due to reduced latency
controller-url: 127.0.0.1:7000

service-id: proxyA
```

## Usage
### Commands
* `/tb` - TerraBungee Information
* `/tba` - TerraBungee Admin Commands

### Permissions
* `terrabungee.admin` - Gives access to TerraBungee admin commands. **Only give this permission to people you trust! Otherwise, you might wake up with no servers left**

## Building
### Controller
* Clone this repo, or download as a zip
* Open the `controller` folder in IntelliJ
* Import the Gradle Project
* Build Using: gradle task - `shadowJar`
* Built jar is located in `/build/libs`

### Proxy
* Clone this repo, or download as a zip
* Open the `proxy` folder in IntelliJ
* Import the Gradle Project
* Build Using: gradle task - `shadowJar`
* Built jar is located in `/build/libs`

### API
* Clone this repo, or download as a zip
* Open the `api` folder in IntelliJ
* Import the Gradle Project
* Build Using: gradle task - `shadowJar`
* Built jar is located in `/build/libs`
* Copy the jar into `/libs` in the root directories of the node, proxy, instance, and controller projects

## Can I use this in my code?
* No. This is closed source, we don't want you here. If you're supposed to be here, then refer to the API module above.

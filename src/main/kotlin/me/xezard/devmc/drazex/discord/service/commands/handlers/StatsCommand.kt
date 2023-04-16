/*
 *  Drazex-Discord
 *  Discord-bot for the project community devmc.org,
 *  designed to automate administrative tasks, notifications
 *  and other functionality related to the functioning of the community
 *  Copyright (C) 2023 Ivan `Xezard` Zotov
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.xezard.devmc.drazex.discord.service.commands.handlers

import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.discordjson.json.UserGuildData
import discord4j.rest.util.Permission
import me.xezard.devmc.drazex.discord.config.DiscordConfiguration
import me.xezard.devmc.drazex.discord.config.properties.RolesProperties
import me.xezard.devmc.drazex.discord.service.app.AppService
import me.xezard.devmc.drazex.discord.service.commands.ICommandHandler
import me.xezard.devmc.drazex.discord.service.messages.MessagesService
import me.xezard.devmc.drazex.discord.service.roles.RolesService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatsCommand (
    private val appService: AppService,
    private val messagesService: MessagesService,
    private val rolesService: RolesService,
    private val discordConfiguration: DiscordConfiguration,
    private val rolesProperties: RolesProperties
): ICommandHandler {
    override fun handle(event: ApplicationCommandInteractionEvent): Mono<Void> {
        val member = event.interaction.member.orElse(null) ?: return Mono.empty()

        return this.rolesService.hasRole(member, this.rolesProperties.admin)
                .filter { it }
                .switchIfEmpty(Mono.empty())
                .flatMap {
                    val replaces = this.appService.replaces.invoke()

                    val uptime = replaces["{uptime}"].toString()
                    val usedMemory = replaces["{used_memory}"].toString()
                    val availableMemory = replaces["{available_memory}"].toString()
                    val maximumMemory = replaces["{maximum_memory}"].toString()

                    val memoryInfo =
                    """
                    Используется: $usedMemory МБ
                    Доступная: $availableMemory МБ
                    Всего: $maximumMemory МБ
                    """.trimIndent()

                    val embed = EmbedCreateSpec.builder()
                            .title("Статистика")
                            .color(this.messagesService.getColorFromString(this.discordConfiguration.messagesColor))
                            .addField("Аптайм", uptime, false)
                            .addField("Память", memoryInfo, false).build()

                    event.reply(InteractionApplicationCommandCallbackSpec.builder()
                            .addEmbed(embed)
                            .build()
                            .withEphemeral(true))
                }
    }

    override fun register(
        discordClient: DiscordClient,
        guild: UserGuildData,
        gateway: GatewayDiscordClient
    ): ApplicationCommandRequest {
        return ApplicationCommandRequest.builder()
                .name(this.name())
                .description("Display the overall statistics of the bot")
                .defaultMemberPermissions(Permission.ADMINISTRATOR.value.toString())
                .build()
    }

    override fun name(): String {
        return "stats"
    }
}
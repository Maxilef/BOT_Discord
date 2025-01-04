package com.example.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class MainBot extends ListenerAdapter {
    public static void main(String[] args) {
        String token = "MTMyNDg3NTc3NzQzMDI2MTgxMQ.GTtbL9.8Kq67gxm30l8o-TKD3aRLZ8ogf6H7e-MAztOgw"; // Remplacez par votre token Discord

        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT) // Active l'accès au contenu des messages
                    .addEventListeners(new MainBot())
                    .setActivity(Activity.playing("!ping pour tester")); // Ajout d'une activité pour votre bot

            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignorer les messages du bot lui-même
        if (event.getAuthor().isBot()) return;

        // Réponse à un message spécifique
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }
    }
}

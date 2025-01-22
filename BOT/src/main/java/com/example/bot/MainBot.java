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
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS) // Active l'intention pour les membres
                    .addEventListeners(new MainBot())
                    .setActivity(Activity.playing("!ping pour tester"));

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

        // Commande !update
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!update")) {
            // Vérifie si le message vient d'un serveur (Guild)
            if (event.isFromGuild()) {
                StringBuilder response = new StringBuilder("Liste des utilisateurs et leurs rôles :\n");

                // Parcourt les membres du serveur
                event.getGuild().loadMembers().onSuccess(members -> {
                    for (var member : members) {
                        // Ajoute le nom d'utilisateur et ses rôles au message
                        response.append(member.getEffectiveName()).append(" : ");
                        if (member.getRoles().isEmpty()) {
                            response.append("Aucun rôle");
                        } else {
                            response.append(member.getRoles().stream()
                                    .map(role -> role.getName())
                                    .reduce((a, b) -> a + ", " + b).orElse("Aucun rôle"));
                        }
                        response.append("\n");
                    }

                    // Envoie la liste dans le canal
                    event.getChannel().sendMessage(response.toString()).queue();
                }).onError(e -> {
                    event.getChannel().sendMessage("Une erreur s'est produite lors du chargement des membres.").queue();
                });
            } else {
                event.getChannel().sendMessage("Cette commande doit être utilisée dans un serveur !").queue();
            }
        }
    }

}

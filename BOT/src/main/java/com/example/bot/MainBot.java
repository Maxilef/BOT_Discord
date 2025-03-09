package com.example.bot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.stream.Collectors;

public class MainBot extends ListenerAdapter {
    private static final String API_URL = "http://localhost:8081/Rest-1.0-SNAPSHOT/users";
    private static final OkHttpClient client = new OkHttpClient();

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

        // Réponse à un message spécifique ici !ping renvoi pong par le bot
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!ping")) {
            event.getChannel().sendMessage("Pong!").queue();
        }

        // Commande !list   list les user et leur roles sur le serveur
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!list")) {
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

        if (event.getMessage().getContentRaw().equalsIgnoreCase("!update")) {
            if (event.isFromGuild()) {
                event.getGuild().loadMembers().onSuccess(members -> {
                    // 1️⃣ Récupérer la liste actuelle des utilisateurs en BDD
                    Request request = new Request.Builder()
                            .url(API_URL)
                            .get()
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            event.getChannel().sendMessage("Erreur lors de la récupération des utilisateurs en BDD.").queue();
                            System.err.println("Erreur : " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                System.err.println("Erreur réponse API : " + response.code());
                                return;
                            }

                            JSONArray dbUsers = new JSONArray(response.body().string());
                            response.close();

                            // 2️⃣ Convertir les membres du serveur en liste de noms
                            var guildMemberNames = members.stream()
                                    .map(member -> member.getEffectiveName())
                                    .collect(Collectors.toSet());

                            // 3️⃣ Vérifier qui n'est plus sur le serveur et le supprimer
                            for (int i = 0; i < dbUsers.length(); i++) {
                                JSONObject user = dbUsers.getJSONObject(i);
                                String userName = user.getString("nom");

                                if (!guildMemberNames.contains(userName)) {
                                    supprimerUtilisateur(userName, event); // Supprime l'utilisateur absent
                                }
                            }

                            // 4️⃣ Ajouter ou mettre à jour les membres existants
                            for (var member : members) {
                                String roles = member.getRoles().stream()
                                        .map(role -> role.getName())
                                        .collect(Collectors.joining(", "));
                                if (roles.isEmpty()) roles = "Aucun rôle";
                                ajouterUtilisateur(member.getEffectiveName(), roles, event);
                            }
                        }
                    });
                }).onError(e -> {
                    event.getChannel().sendMessage("Erreur lors du chargement des membres.").queue();
                });
            } else {
                event.getChannel().sendMessage("Cette commande doit être utilisée dans un serveur !").queue();
            }
        }
    }

    private void ajouterUtilisateur(String nom, String roles, MessageReceivedEvent event) {
        JSONObject json = new JSONObject();
        json.put("nom", nom);
        json.put("email", nom.toLowerCase().replace(" ", "") + "@mail.com");  // Génère un email unique

        // Vérifie si des rôles existent avant de les ajouter
        JSONArray rolesArray = new JSONArray();
        if (roles != null && !roles.isEmpty()) {
            for (String role : roles.split("\\s*,\\s*")) {  // Gère les espaces autour de la virgule
                if (!role.isEmpty()) {  // Évite les rôles vides
                    JSONObject roleObject = new JSONObject();
                    roleObject.put("nom", role);
                    rolesArray.put(roleObject);
                }
            }
        }
        json.put("roles", rolesArray);

        // 🔥 Ajout du log pour voir ce qui est envoyé
        System.out.println("JSON envoyé pour " + nom + " : " + json.toString(2));

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                event.getChannel().sendMessage("Erreur lors de l'ajout de " + nom + " : " + e.getMessage()).queue();
                System.err.println("Erreur réseau : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        event.getChannel().sendMessage("Utilisateur " + nom + " ajouté avec succès !").queue();
                    } else {
                        String errorResponse = response.body().string();
                        event.getChannel().sendMessage("Erreur lors de l'ajout de " + nom + ". Code: " + response.code()).queue();
                        System.err.println("Réponse serveur : " + errorResponse);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la lecture de la réponse : " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }

    private void supprimerUtilisateur(String nom, MessageReceivedEvent event) {
        Request request = new Request.Builder()
                .url(API_URL + "/" + nom) // Supposons que l'API accepte une suppression par nom
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Erreur lors de la suppression de " + nom + " : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Utilisateur " + nom + " supprimé de la BDD.");
                } else {
                    System.err.println("Erreur lors de la suppression de " + nom + " : " + response.code());
                }
                response.close();
            }
        });
    }


}

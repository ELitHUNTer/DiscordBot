import net.dv8tion.jda.client.events.call.voice.CallVoiceJoinEvent;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import net.dv8tion.jda.core.utils.WidgetUtil;

import javax.security.auth.login.LoginException;
import java.io.*;

public class Main extends ListenerAdapter {

    private char prefix = '-';

    public static void main(String[] args){
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String token = "NzEyNjczOTUzNDM2NDAxNzk1.XseG0A.Nnte6WksneZ-KaftUZfcvNyDfXg1";
        token = token.substring(0, token.length() - 1);
        System.out.println(token);
        builder.setToken(token);
        //builder.setAudioEnabled(true);
        //builder.setAutoReconnect(false);
        builder.addEventListener(new Main());
        try {
            builder.buildAsync();
        } catch (LoginException e) {
            System.out.println("Login Exception");
            //e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //super.onMessageReceived(event);
        if (event.getAuthor().isBot()) return;
        System.out.println("Сообщение от " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());


        String message = event.getMessage().getContentRaw();
        if (message.charAt(0) == prefix){
            message = message.substring(1);
            int spaceInd = message.indexOf(' ');
            if (spaceInd == -1) spaceInd = message.length();

            String command = message.substring(0, spaceInd);
            if (command.equals("echo")){
                message = message.substring(message.indexOf(' '));
                event.getChannel().sendMessage(message).queue();
            } else if (command.equals("join")){
                TextChannel channel = event.getTextChannel();
                //Permission[] channel;
                if(!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
                    // The bot does not have permission to join any voice channel. Don't forget the .queue()!
                    channel.sendMessage("I do not have permissions to join a voice channel!").queue();
                    return;
                }
                // Creates a variable equal to the channel that the user is in.
                VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
                // Checks if they are in a channel -- not being in a channel means that the variable = null.
                if(connectedChannel == null) {
                    // Don't forget to .queue()!
                    channel.sendMessage("You are not connected to a voice channel!").queue();
                    return;
                }
                // Gets the audio manager.
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.setConnectTimeout(10000000);

                audioManager.setAutoReconnect(false);
                // When somebody really needs to chill.
                if(audioManager.isAttemptingToConnect()) {
                    channel.sendMessage("The bot is already trying to connect! Enter the chill zone!").queue();
                    return;
                }
                // Connects to the channel.
                audioManager.openAudioConnection(connectedChannel);
                // Obviously people do not notice someone/something connecting.
                channel.sendMessage("Connected to the voice channel!").queue();
            } else if (command.equals("leave")){
                TextChannel channel = event.getTextChannel();
                // Gets the channel in which the bot is currently connected.
                VoiceChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
                // Checks if the bot is connected to a voice channel.
                if(connectedChannel == null) {
                    // Get slightly fed up at the user.
                    channel.sendMessage("I am not connected to a voice channel!").queue();
                    return;
                }
                // Disconnect from the channel.
                event.getGuild().getAudioManager().closeAudioConnection();
                // Notify the user.
                channel.sendMessage("Disconnected from the voice channel!").queue();
            } else if (command.equals("hentai")){
                event.getChannel().sendFile(new File("C:\\niki_avatar.jpg")).queue();
            } else {
                event.getChannel().sendMessage("unknown command").queue();
            }
        }
    }
}

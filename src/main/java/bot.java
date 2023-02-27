import events.event;

import java.io.IOException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class bot {

    public static void main(String[] args) throws IOException, InterruptedException {

event ev = new event();
        JDA jda = JDABuilder.createDefault("/*bot token*/")
                .setActivity(Activity.listening("silence"))
                .addEventListeners(ev)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .build();

        // Sets the global command list to the provided commands
        OptionData op1 = new OptionData(OptionType.CHANNEL, "channel" ,"Input channel ID.",true);
        OptionData op2= new OptionData(OptionType.INTEGER, "number" ,"Input number of messages to get urls from.",true);

        jda.updateCommands().addCommands(
               Commands.slash("download", "download all attachments of the channel").addOptions(op1,op2)
        ).queue();

        jda.awaitReady();//waiting for the bot to build
        ev.jda = jda;//passing jda value to ev (event class )
    }
//end main

    }//end bot


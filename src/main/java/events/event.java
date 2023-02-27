package events;

import me.tongfei.progressbar.ProgressBar;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class event extends ListenerAdapter {
    public JDA jda;


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // make sure we handle the right command
        switch (event.getName()) {

            case "download":
                File urlsFile = new File("urls.txt");
                File downloadDirectory = new File("D:/OutPut");//this code doesnt make directory if it doesnt exist
                event.reply("Sure").queue();


                // pass jda to MyeventHandler
                MyEventHandler eventHandler = new MyEventHandler(jda);

//get channel name , and the number from options of the commande
                int Nmbr = event.getOption("number").getAsInt();
                String ch;
                OptionMapping msgoption = event.getOption("channel");
                ch = msgoption.getAsChannel().asTextChannel().getManager().getChannel().getId();
//                System.out.println(ch);
                if(event.getMember().getUser().getName().equalsIgnoreCase("discord username")){ //write your discord username if you will host the bot in your machine , so it directly download the file if you used the /download commande , you will find the downloaded files in the directory you specified in the downloadDirectory variable
                    try {

                       eventHandler.handleEvent(ch,Nmbr); //channel and number of imgs as parms ,to extract urls and save to file before downloading the files

                        downloadFilesFromUrls(urlsFile, downloadDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else
                {
                    try {
                        eventHandler.handleEvent(ch,Nmbr); //channel and number of imgs as parms //to extract urls and save to file before replying to user with the file
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //
                    event.getChannel().sendMessage("Here are the attachment URLs").addFiles(FileUpload.fromData(urlsFile)).queue();
                }
                break;


        }
    }

    //class event handler " extract and write to file"
    public static class MyEventHandler {
        String FILE_NAME = "urls.txt";
        File file = new File(FILE_NAME);

        private final JDA jda;

        public MyEventHandler(JDA jda) {
            this.jda = jda;
        }

        public void handleEvent(String chaNNelId,int x) throws IOException {
            TextChannel channel = jda.getTextChannelById(chaNNelId); // chosen channel by user
            FileWriter writer = new FileWriter(file);

            List<Message> history = channel.getHistory().retrievePast(x).complete();//retrievePast(x) retrieve x past messages
            for (Message message : history) {
                for (Message.Attachment attachment : message.getAttachments()) {
                    String url = attachment.getUrl();//get url of the attachments in the message
//                    System.out.println(url);
                    writer.write(url + "\n");
                }
            }
            writer.close();//
       }

    }//handleEvent class



    //
    public static void downloadFilesFromUrls(@NotNull File urlsFile, @NotNull File downloadDir) throws IOException {
        // Read the URLs from the file
        List<String> urls = FileUtils.readLines(urlsFile, StandardCharsets.UTF_8);

        // Initialize the progress bar
        ProgressBar progressBar = new ProgressBar("Downloading", urls.size());

        int countSuccess = 0;
        int countFail = 0;

        for (String url : urls) {
            String fileName = FilenameUtils.getName(url);

            try {
                // Download the file from the URL
                URL fileUrl = new URL(url);
                InputStream inputStream = fileUrl.openStream();
                FileOutputStream outputStream = new FileOutputStream(new File(downloadDir, fileName));
                IOUtils.copy(inputStream, outputStream);
                inputStream.close();
                outputStream.close();
                countSuccess++;
            } catch (IOException e) {
                countFail++;
                System.err.println("Failed to download " + url + ": " + e.getMessage());
            }

            progressBar.step();
        }

        progressBar.close();

        System.out.println("Downloaded " + countSuccess + " files");
        if (countFail > 0) {
            System.err.println("Failed to download " + countFail + " files");
        }
    }

//
}

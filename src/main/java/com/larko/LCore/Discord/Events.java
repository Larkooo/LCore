package com.larko.LCore.Discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Events extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        SelfUser self = event.getJDA().getSelfUser();
        System.out.println("Bot is ready " + self.getAsTag());
    }
}

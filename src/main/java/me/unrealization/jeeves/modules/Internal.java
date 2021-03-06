package me.unrealization.jeeves.modules;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import me.unrealization.jeeves.bot.Jeeves;
import me.unrealization.jeeves.bot.MessageQueue;
import me.unrealization.jeeves.bot.RoleQueue;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import me.unrealization.jeeves.interfaces.BotCommand;
import me.unrealization.jeeves.interfaces.BotModule;

public class Internal extends BotModule
{
	public Internal()
	{
		this.version = "1.0.0";

		this.commandList = new String[31];
		this.commandList[0] = "Help";
		this.commandList[1] = "Version";
		this.commandList[2] = "Ping";
		this.commandList[3] = "GetUtcTime";
		this.commandList[4] = "Shutdown";
		this.commandList[5] = "GetDebugging";
		this.commandList[6] = "SetDebugging";
		this.commandList[7] = "GetServers";
		this.commandList[8] = "GetCommandPrefix";
		this.commandList[9] = "SetCommandPrefix";
		this.commandList[10] = "GetRespondOnPrefix";
		this.commandList[11] = "SetRespondOnPrefix";
		this.commandList[12] = "GetRespondOnMention";
		this.commandList[13] = "SetRespondOnMention";
		this.commandList[14] = "GetIgnoredChannels";
		this.commandList[15] = "AddIgnoredChannel";
		this.commandList[16] = "RemoveIgnoredChannel";
		this.commandList[17] = "GetIgnoredUsers";
		this.commandList[18] = "AddIgnoredUser";
		this.commandList[19] = "RemoveIgnoredUser";
		this.commandList[20] = "GetIgnoredRoles";
		this.commandList[21] = "AddIgnoredRole";
		this.commandList[22] = "RemoveIgnoredRole";
		this.commandList[23] = "GetModules";
		this.commandList[24] = "EnableModule";
		this.commandList[25] = "DisableModule";
		this.commandList[26] = "GetServerId";
		this.commandList[27] = "GetChannelId";
		this.commandList[28] = "GetRoleId";
		this.commandList[29] = "GetUserId";
		this.commandList[30] = "GetJoinLink";

		this.defaultConfig.put("commandPrefix", "!");
		this.defaultConfig.put("respondOnPrefix", "0");
		this.defaultConfig.put("respondOnMention", "1");
		this.defaultConfig.put("ignoredChannels", new ArrayList<String>());
		this.defaultConfig.put("ignoredUsers", new ArrayList<String>());
		this.defaultConfig.put("ignoredRoles", new ArrayList<String>());
		this.defaultConfig.put("disabledModules", new ArrayList<String>());
	}

	@Override
	public boolean canDisable()
	{
		return false;
	}

	public static class Help extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get help.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "[module|command]";
			return output;
		}

		@Override
		public void execute(IMessage message, String searchName)
		{
			boolean sendInChannel = false;
			String output = "";
			String[] moduleList = Jeeves.getModuleList();

			for (int moduleIndex = 0; moduleIndex < moduleList.length; moduleIndex++)
			{
				if ((searchName.isEmpty() == false) && (searchName.equals(moduleList[moduleIndex]) == false))
				{
					continue;
				}

				BotModule module = Jeeves.getModule(moduleList[moduleIndex]);

				if (Jeeves.isDisabled(message.getGuild().getLongID(), module) == true)
				{
					continue;
				}

				output += "**" + moduleList[moduleIndex] + " functions**\n\n";
				output += module.getHelp() + "\n";
			}

			if (output.isEmpty() == true)
			{
				sendInChannel = true;

				for (int moduleIndex = 0; moduleIndex < moduleList.length; moduleIndex++)
				{
					BotModule module = Jeeves.getModule(moduleList[moduleIndex]);

					if (Jeeves.isDisabled(message.getGuild().getLongID(), module) == true)
					{
						continue;
					}

					String[] commandList = module.getCommands();

					if (commandList == null)
					{
						continue;
					}

					for (int commandIndex = 0; commandIndex < commandList.length; commandIndex++)
					{
						if (commandList[commandIndex].toLowerCase().equals(searchName.toLowerCase()) == false)
						{
							continue;
						}

						output += module.getHelp(commandList[commandIndex]);
					}
				}
			}

			if (output.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "I'm sorry, but I cannot help you with " + searchName);
				return;
			}

			if (sendInChannel == true)
			{
				MessageQueue.sendMessage(message.getChannel(), output);
				return;
			}

			MessageQueue.sendMessage(message.getAuthor(), output);
			MessageQueue.sendMessage(message.getChannel(), "Help sent as private message.");
		}
	}

	public static class Version extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Show the bot's version number.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			MessageQueue.sendMessage(message.getChannel(), "Jeeves " + Jeeves.version);
		}
	}

	public static class Ping extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Check if the bot is alive.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			MessageQueue.sendMessage(message.getChannel(), "Pong!");
		}
	}

	public static class GetUtcTime extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the current UTC time.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			String utcTime = Jeeves.getUtcTime();
			MessageQueue.sendMessage(message.getChannel(), "The current UTC time is: " + utcTime);
		}
	}

	public static class Shutdown extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Shut down the bot.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public boolean owner()
		{
			return true;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			try
			{
				Scheduler scheduler = new StdSchedulerFactory().getScheduler();
				scheduler.shutdown();
			}
			catch (SchedulerException e)
			{
				Jeeves.debugException(e);
			}

			MessageQueue.sendMessage(message.getChannel(), "Good bye, cruel world.");
			MessageQueue messageQueue = MessageQueue.getInstance();
			RoleQueue roleQueue = RoleQueue.getInstance();

			while ((messageQueue.isWorking() == true) || (roleQueue.isWorking() == true))
			{
				//wait for the queues to finish processing
			}

			try
			{
				message.getClient().logout();
			}
			catch (DiscordException e)
			{
				Jeeves.debugException(e);
			}
		}
	}

	public static class GetDebugging extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Check if debugging is enabled.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public boolean owner()
		{
			return true;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			String debugging = (String)Jeeves.clientConfig.getValue("debugging");

			if (debugging.equals("0") == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "Debugging is disabled.");
			}
			else
			{
				MessageQueue.sendMessage(message.getChannel(), "Debugging is enabled.");
			}
		}
	}

	public static class SetDebugging extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Enable/disable debugging.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<1|0>";
			return output;
		}

		@Override
		public boolean owner()
		{
			return true;
		}

		@Override
		public void execute(IMessage message, String debugging)
		{
			if ((debugging.equals("0") == false) && (debugging.equals("1") == false))
			{
				MessageQueue.sendMessage(message.getChannel(), "Invalid value");
				return;
			}

			Jeeves.clientConfig.setValue("debugging", debugging);

			try
			{
				Jeeves.clientConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			if (debugging.equals("0") == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "Debugging has been disabled.");
			}
			else
			{
				MessageQueue.sendMessage(message.getChannel(), "Debugging has been enabled.");
			}
		}
	}

	public static class GetServers extends BotCommand
	{

		@Override
		public String getHelp()
		{
			String output = "Get the list of servers the bot is connected to.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public boolean owner()
		{
			return true;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			List<IGuild> serverList = message.getClient().getGuilds();
			String output = "The bot is connected to the following servers:\n\n";

			for (int serverIndex = 0; serverIndex < serverList.size(); serverIndex++)
			{
				IGuild server = serverList.get(serverIndex);
				output += "\t" + server.getName() + "\n";
			}

			MessageQueue.sendMessage(message.getChannel(), output);
		}
	}

	public static class GetCommandPrefix extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the current command prefix.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			String commandPrefix = (String)Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "commandPrefix");
			MessageQueue.sendMessage(message.getChannel(), "The command prefix is: " + commandPrefix);
		}
	}

	public static class SetCommandPrefix extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Set the command prefix.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<prefix>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String commandPrefix)
		{
			if (commandPrefix.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The command prefix cannot be empty.");
				return;
			}

			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "commandPrefix", commandPrefix);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The command prefix has been set to: " + commandPrefix);
		}
	}

	public static class GetRespondOnPrefix extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Check if the bot will respond to messages starting with the command prefix.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			String respondOnPrefix = (String)Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "respondOnPrefix");

			if (respondOnPrefix.equals("0") == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot will not respond to the command prefix.");
			}
			else
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot will respond to the command prefix.");
			}
		}
	}

	public static class SetRespondOnPrefix extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Set whether or not the bot will respond to messages starting with the command prefix.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<1|0>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String respondOnPrefix)
		{
			if ((respondOnPrefix.equals("0") == false) && (respondOnPrefix.equals("1") == false))
			{
				MessageQueue.sendMessage(message.getChannel(), "Invalid value");
				return;
			}

			System.out.println(respondOnPrefix);
			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "respondOnPrefix", respondOnPrefix);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			if (respondOnPrefix.equals("0") == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot will no longer respond to the command prefix.");
			}
			else
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot now responds to the command prefix.");
			}
		}
	}

	public static class GetRespondOnMention extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Check if the bot will respond to mentions.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			String respondOnMention = (String)Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "respondOnMention");

			if (respondOnMention.equals("0") == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot will not respond to mentions.");
			}
			else
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot will respond to mentions.");
			}
		}
	}

	public static class SetRespondOnMention extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Set whether or not the bot will respond to mentions.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<1|0>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String respondOnMention)
		{
			if ((respondOnMention.equals("0") == false) && (respondOnMention.equals("1") == false))
			{
				MessageQueue.sendMessage(message.getChannel(), "Invalid value");
				return;
			}

			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "respondOnMention", respondOnMention);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			if (respondOnMention.equals("0") == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot will no longer respond to mentions.");
			}
			else
			{
				MessageQueue.sendMessage(message.getChannel(), "The bot now responds to mentions.");
			}
		}
	}

	public static class GetIgnoredChannels extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the list of ignored channels.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			Object ignoredChannels = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredChannels");

			if (ignoredChannels.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "No channels are being ignored.");
				return;
			}

			List<String> ignoredChannelList = Jeeves.listToStringList((List<?>)ignoredChannels);

			if (ignoredChannelList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "No channels are being ignored.");
				return;
			}

			String output = "The following channels are being ignored:\n\n";

			for (int channelIndex = 0; channelIndex < ignoredChannelList.size(); channelIndex++)
			{
				Long channelId = Long.parseLong(ignoredChannelList.get(channelIndex));
				IChannel channel = message.getGuild().getChannelByID(channelId);
				output += channel.getName() + "\n";
			}

			MessageQueue.sendMessage(message.getChannel(), output);
		}
	}

	public static class AddIgnoredChannel extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Add a channel to the ignore list.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<channel>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String channelName)
		{
			if (channelName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a channel name.");
				return;
			}

			IChannel channel = Jeeves.findChannel(message.getGuild(), channelName);

			if (channel == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the channel " + channelName);
				return;
			}

			if (Jeeves.isIgnored(channel) == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The channel " + channel.getName() + " is being ignored already.");
				return;
			}

			Object ignoredChannels = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredChannels");
			List<String> ignoredChannelList;

			if (ignoredChannels.getClass() == String.class)
			{
				ignoredChannelList = new ArrayList<String>();
			}
			else
			{
				ignoredChannelList = Jeeves.listToStringList((List<?>)ignoredChannels);
			}

			String channelIdString = Long.toString(channel.getLongID());
			ignoredChannelList.add(channelIdString);
			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "ignoredChannels", ignoredChannelList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following channel is now being ignored: " + channel.getName());
		}
	}

	public static class RemoveIgnoredChannel extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Remove a channel from the ignore list.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<channel>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String channelName)
		{
			if (channelName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a channel name.");
				return;
			}

			IChannel channel = Jeeves.findChannel(message.getGuild(), channelName);

			if (channel == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the channel " + channelName);
				return;
			}

			Object ignoredChannels = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredChannels");

			if (ignoredChannels.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "No channels are being ignored.");
				return;
			}

			List<String> ignoredChannelList = Jeeves.listToStringList((List<?>)ignoredChannels);

			if (ignoredChannelList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "No channels are being ignored.");
				return;
			}

			String channelIdString = Long.toString(channel.getLongID());
			boolean removed = ignoredChannelList.remove(channelIdString);

			if (removed == false)
			{
				MessageQueue.sendMessage(message.getChannel(), "The channel " + channel.getName() + " is not being ignored.");
				return;
			}

			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "ignoredChannels", ignoredChannelList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following channel is no longer being ignored: " + channel.getName());
		}
	}

	public static class GetIgnoredUsers extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the list of ignored users.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			Object ignoredUsers = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredUsers");

			if (ignoredUsers.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "No users are being ignored.");
				return;
			}

			List<String> ignoredUserList = Jeeves.listToStringList((List<?>)ignoredUsers);

			if (ignoredUserList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "No users are being ignored.");
				return;
			}

			String output = "The following users are being ignored:\n\n";

			for (int userIndex = 0; userIndex < ignoredUserList.size(); userIndex++)
			{
				long userId = Long.parseLong(ignoredUserList.get(userIndex));
				IUser user = message.getGuild().getUserByID(userId);
				output += user.getName() + "\n";
			}

			MessageQueue.sendMessage(message.getChannel(), output);
		}
	}

	public static class AddIgnoredUser extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Add a user to the ignore list.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<user>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String userName)
		{
			if (userName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a user name.");
				return;
			}

			IUser user = Jeeves.findUser(message.getGuild(), userName);

			if (user == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the user " + userName);
				return;
			}

			if (Jeeves.isIgnored(message.getGuild().getLongID(), user) == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The user " + user.getName() + " is being ignored already.");
				return;
			}

			Object ignoredUsers = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredUsers");
			List<String> ignoredUserList;

			if (ignoredUsers.getClass() == String.class)
			{
				ignoredUserList = new ArrayList<String>();
			}
			else
			{
				ignoredUserList = Jeeves.listToStringList((List<?>)ignoredUsers);
			}

			String userIdString = Long.toString(user.getLongID());
			ignoredUserList.add(userIdString);
			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "ignoredUsers", ignoredUserList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following user is now being ignored: " + user.getName());
		}
	}

	public static class RemoveIgnoredUser extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Remove a user from the ignore list.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<user>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String userName)
		{
			if (userName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a user name.");
				return;
			}

			IUser user = Jeeves.findUser(message.getGuild(), userName);

			if (user == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the user " + userName);
				return;
			}

			Object ignoredUsers = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredUsers");

			if (ignoredUsers.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "No users are being ignored.");
				return;
			}

			List<String> ignoredUserList = Jeeves.listToStringList((List<?>)ignoredUsers);

			if (ignoredUserList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "No users are being ignored.");
				return;
			}

			String userIdString = Long.toString(user.getLongID());
			boolean removed = ignoredUserList.remove(userIdString);

			if (removed == false)
			{
				MessageQueue.sendMessage(message.getChannel(), "The user " + user.getName() + " is not being ignored.");
				return;
			}

			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "ignoredUsers", ignoredUserList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following user is no longer being ignored: " + user.getName());
		}
	}

	public static class GetIgnoredRoles extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the list of ignored roles.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			Object ignoredRoles = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredRoles");

			if (ignoredRoles.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "No roles are being ignored.");
				return;
			}

			List<String> ignoredRoleList = Jeeves.listToStringList((List<?>)ignoredRoles);

			if (ignoredRoleList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "No roles are being ignored.");
				return;
			}

			String output = "The following roles are being ignored:\n\n";

			for (int index = 0; index < ignoredRoleList.size(); index++)
			{
				long roleId = Long.parseLong(ignoredRoleList.get(index));
				IRole role = message.getGuild().getRoleByID(roleId);
				output += role.getName() + "\n";
			}

			MessageQueue.sendMessage(message.getChannel(), output);
		}
	}

	public static class AddIgnoredRole extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Add a role to the ignore list.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<role>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String roleName)
		{
			if (roleName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a role name.");
				return;
			}

			IRole role = Jeeves.findRole(message.getGuild(), roleName);

			if (role == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the role " + roleName);
				return;
			}

			if (Jeeves.isIgnored(role) == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The role " + role.getName() + " is being ignored already.");
				return;
			}

			Object ignoredRoles = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "getIgnoredRoles");
			List<String> ignoredRoleList;

			if (ignoredRoles.getClass() == String.class)
			{
				ignoredRoleList = new ArrayList<String>();
			}
			else
			{
				ignoredRoleList = Jeeves.listToStringList((List<?>)ignoredRoles);
			}

			String roleIdString = Long.toString(role.getLongID());
			ignoredRoleList.add(roleIdString);
			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "ignoredRoles", ignoredRoleList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following role is now being ignored: " + role.getName());
		}
	}

	public static class RemoveIgnoredRole extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Remove a role from the ignore list.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<role>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String roleName)
		{
			if (roleName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a role name.");
				return;
			}

			IRole role = Jeeves.findRole(message.getGuild(), roleName);

			if (role == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the role " + roleName);
				return;
			}

			Object ignoredRoles = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "ignoredRoles");

			if (ignoredRoles.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "No roles are being ignored.");
				return;
			}

			List<String> ignoredRoleList = Jeeves.listToStringList((List<?>)ignoredRoles);

			if (ignoredRoleList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "No roles are being ignored.");
				return;
			}

			String roleIdString = Long.toString(role.getLongID());
			boolean removed = ignoredRoleList.remove(roleIdString);

			if (removed == false)
			{
				MessageQueue.sendMessage(message.getChannel(), "The role " + role.getName() + " is not being ignored.");
				return;
			}

			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "ignoredRoles", ignoredRoleList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following role is no longer being ignored: " + role.getName());
		}
	}

	public static class GetModules extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the list of available modules.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentList)
		{
			String[] moduleList = Jeeves.getModuleList();
			String output = "The following modules are available:\n\n";

			for (int moduleIndex = 0; moduleIndex < moduleList.length; moduleIndex++)
			{
				BotModule module = Jeeves.getModule(moduleList[moduleIndex]);
				output += "\t" + moduleList[moduleIndex] + " " + module.getVersion();

				if (Jeeves.isDisabled(message.getGuild().getLongID(), module) == true)
				{
					output += " (disabled)";
				}

				output += "\n";
			}

			MessageQueue.sendMessage(message.getChannel(), output);
		}
	}

	public static class EnableModule extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Enable a module.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<module>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String moduleName)
		{
			if (moduleName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a module name.");
				return;
			}

			BotModule module = Jeeves.getModule(moduleName);

			if (module == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the module " + moduleName);
				return;
			}

			Long discordId = module.getDiscordId();

			if ((discordId != null) && (discordId.equals(message.getGuild().getLongID()) == false))
			{
				MessageQueue.sendMessage(message.getChannel(), "The module " + moduleName + " is not available on this server.");
				return;
			}

			Object disabledModules = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "disabledModules");

			if (disabledModules.getClass() == String.class)
			{
				MessageQueue.sendMessage(message.getChannel(), "All available modules are enabled.");
				return;
			}

			List<String> disabledModuleList = Jeeves.listToStringList((List<?>)disabledModules);

			if (disabledModuleList.size() == 0)
			{
				MessageQueue.sendMessage(message.getChannel(), "All available modules are enabled.");
				return;
			}

			boolean removed = disabledModuleList.remove(moduleName);

			if (removed == false)
			{
				MessageQueue.sendMessage(message.getChannel(), "The module " + moduleName +  " is not disabled.");
				return;
			}

			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "disabledModules", disabledModuleList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following module has been enabled: " + moduleName);
		}
	}

	public static class DisableModule extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Disable a module.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<module>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String moduleName)
		{
			if (moduleName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to provide a module name.");
				return;
			}

			BotModule module = Jeeves.getModule(moduleName);

			if (module == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the module " + moduleName);
				return;
			}

			if (module.canDisable() == false)
			{
				MessageQueue.sendMessage(message.getChannel(), "The module " + moduleName + " cannot be disabled.");
				return;
			}

			if (Jeeves.isDisabled(message.getGuild().getLongID(), module) == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "The module " + moduleName + " is disabled already.");
				return;
			}

			Object disabledModules = Jeeves.serverConfig.getValue(message.getGuild().getLongID(), "disabledModules");
			List<String> disabledModuleList;

			if (disabledModules.getClass() == String.class)
			{
				disabledModuleList = new ArrayList<String>();
			}
			else
			{
				disabledModuleList = Jeeves.listToStringList((List<?>)disabledModules);
			}

			disabledModuleList.add(moduleName);
			Jeeves.serverConfig.setValue(message.getGuild().getLongID(), "disabledModules", disabledModuleList);

			try
			{
				Jeeves.serverConfig.saveConfig();
			}
			catch (ParserConfigurationException | TransformerException e)
			{
				Jeeves.debugException(e);
				MessageQueue.sendMessage(message.getChannel(), "Cannot store the setting.");
				return;
			}

			MessageQueue.sendMessage(message.getChannel(), "The following module has been disabled: " + moduleName);
		}
	}

	public static class GetServerId extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the ID of this Discord server.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			String idString = Long.toString(message.getGuild().getLongID());
			MessageQueue.sendMessage(message.getChannel(), "The ID of this Discord server is " + idString);
		}
	}

	public static class GetChannelId extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the ID of the given or current channel.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "[channel]";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String channelName)
		{
			IChannel channel = null;

			if (channelName.isEmpty() == true)
			{
				channel = message.getChannel();
			}
			else
			{
				channel = Jeeves.findChannel(message.getGuild(), channelName);

				if (channel == null)
				{
					MessageQueue.sendMessage(message.getChannel(), "Cannot find the channel " + channelName);
					return;
				}
			}

			String idString = Long.toString(channel.getLongID());
			MessageQueue.sendMessage(message.getChannel(), "The ID of the channel " + channel.getName() + " is " + idString);
		}
	}

	public static class GetRoleId extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the ID of the given role.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "<role>";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String roleName)
		{
			if (roleName.isEmpty() == true)
			{
				MessageQueue.sendMessage(message.getChannel(), "You need to supply a role name.");
				return;
			}

			IRole role = Jeeves.findRole(message.getGuild(), roleName);

			if (role == null)
			{
				MessageQueue.sendMessage(message.getChannel(), "Cannot find the role " + roleName);
				return;
			}

			String idString = Long.toString(role.getLongID());
			MessageQueue.sendMessage(message.getChannel(), "The ID of the role " + role.getName() + " is " + idString);
		}
	}

	public static class GetUserId extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the ID of the given user or yourself.";
			return output;
		}

		@Override
		public String getParameters()
		{
			String output = "[user]";
			return output;
		}

		@Override
		public Permissions[] permissions()
		{
			Permissions[] permissionList = new Permissions[1];
			permissionList[0] = Permissions.MANAGE_SERVER;
			return permissionList;
		}

		@Override
		public void execute(IMessage message, String userName)
		{
			IUser user = null;

			if (userName.isEmpty() == true)
			{
				user = message.getAuthor();
			}
			else
			{
				user = Jeeves.findUser(message.getGuild(), userName);

				if (user == null)
				{
					MessageQueue.sendMessage(message.getChannel(), "Cannot find the user " + userName);
					return;
				}
			}

			String idString = Long.toString(user.getLongID());
			MessageQueue.sendMessage(message.getChannel(), "The ID of the user " + user.getName() + " is " + idString);
		}
	}

	public static class GetJoinLink extends BotCommand
	{
		@Override
		public String getHelp()
		{
			String output = "Get the link to let the bot join a new Discord server.";
			return output;
		}

		@Override
		public String getParameters()
		{
			return null;
		}

		@Override
		public boolean owner()
		{
			return true;
		}

		@Override
		public void execute(IMessage message, String argumentString)
		{
			MessageQueue.sendMessage(message.getChannel(), "https://discordapp.com/api/oauth2/authorize?client_id=" + Jeeves.bot.getApplicationClientID() + "&scope=bot");
		}
	}
}

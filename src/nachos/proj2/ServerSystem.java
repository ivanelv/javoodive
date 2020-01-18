package nachos.proj2;

import java.awt.Toolkit;

import nachos.machine.Machine;
import nachos.machine.NetworkLink;
import nachos.machine.Packet;
import nachos.proj1.MainSystem;
import nachos.proj1.Mediator;
import nachos.proj1.ObservableSystem;
import nachos.proj1.facades.MessageFacade;
import nachos.proj1.models.TextMessage;
import nachos.proj1.utilities.Console;
import nachos.proj1.utilities.DateHelper;
import nachos.threads.Semaphore;

public class ServerSystem implements ObservableSystem
{
	private Console console;
	private Semaphore sem;
	private NetworkLink nl;
	private Mediator mediator;
	
	public ServerSystem(Mediator mediator)
	{
		this.mediator = mediator;
		this.console = Console.getInstance();
		this.sem = new Semaphore(0);
		this.nl = Machine.networkLink();
		this.nl.setInterruptHandlers(
				new ReceiveInterruptHandler(), 
				new SendInterruptHandler());
		boot();
	}

	@Override
	public void boot()
	{
		console.println("Server Booted Up.");
		console.printLineSeparator();
		Toolkit.getDefaultToolkit().beep();
		console.read();
	}

	@Override
	public Console getConsole()
	{
		return this.console;
	}

	@Override
	public Semaphore getSemaphore()
	{
		return this.sem;
	}

	@Override
	public NetworkLink getNetworkLink()
	{
		return this.nl;
	}

	@Override
	public void displayReceivedMessage(TextMessage message)
	{
		System.out.printf("%s | %s :\n%s\n\n",
				DateHelper.getCurrentFormattedDate(),
				message.getUser().getUsername(),
				message.getContent());
	}
	
	class ReceiveInterruptHandler implements Runnable
	{
		@Override
		public void run()
		{
			Packet packet = nl.receive();
			String rawData = new String(packet.contents);
			TextMessage message = MessageFacade.getInstance().parseTextMessage(rawData);
			
			System.out.println(packet.srcLink + " (src) : (dst) " + packet.dstLink);
			System.out.println(message.getContent());
			
			if (packet.srcLink == MainSystem.MEDIATOR_ADDRESS)
			{
				System.out.println("Display Received Message");
				displayReceivedMessage(message);
			}
			else
			{
				System.out.println("Broadcast Cuyyy");
				mediator.broadcast(message);
				// Process Command
			}
			
			sem.V();
		}
	}
	
	class SendInterruptHandler implements Runnable
	{
		@Override
		public void run()
		{
			sem.V();
		}
	}
}

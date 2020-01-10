package nachos.proj1;

import org.yaml.snakeyaml.Yaml;

import nachos.machine.Machine;
import nachos.machine.NetworkLink;
import nachos.machine.OpenFile;
import nachos.proj1.models.UserYamlConstructor;
import nachos.proj1.repository.UserRepository;
import nachos.proj1.utilities.Concealer;
import nachos.proj1.utilities.Console;
import nachos.proj2.ServerSystem;

public class MainSystem
{
	private static final int SERVER_ADDRESS = 0;
	private static final String USER_FILENAME = "users.b40";
	private Console console;
	private NetworkLink nl;

	public MainSystem()
	{
		boot();
		loadUsers();
		autoLoginByLinkAddress();
	}

	private void boot()
	{
		console = Console.getInstance();
		nl = Machine.networkLink();
	}

	private void loadUsers()
	{
		OpenFile file = Machine.stubFileSystem().open(USER_FILENAME, false);
		byte[] bytes = new byte[file.length()];
		file.read(bytes, 0, bytes.length);
		file.close();

		String encodedText = new String(bytes);
		String decodedText = Concealer.getInstance().decode(encodedText);
		
		Yaml yaml = new Yaml(new UserYamlConstructor());
		UserRepository.load(yaml.load(decodedText));
	}

	private void autoLoginByLinkAddress()
	{
		if (this.nl.getLinkAddress() == SERVER_ADDRESS)
		{
			new ServerSystem();
		}
	}
}

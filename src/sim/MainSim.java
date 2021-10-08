package sim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainSim {
	private static Cache c;
	
	
	public static void main (String[] args) throws NumberFormatException, IOException {

		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		System.out.println(" WELCOME TO CACHE SIMULATOR! \n"
				+ "=-=-=-=-=-=-=-=-=-=-=-=-=-=-= \n \n");

		System.out.println("Please input a word size (4 or 8 bytes): ");
		int wordsize = Integer.parseInt(console.readLine());

		while ((wordsize != 4) && (wordsize != 8)) {
			System.out.println("The input value is invalid. Please try again: ");
			wordsize = Integer.parseInt(console.readLine());
		}



		System.out.println("\nPlease input a block size (32 or 64 bytes): ");
		int blocksize = Integer.parseInt(console.readLine());

		while ((blocksize != 32) && (blocksize != 64)) {
			System.out.println("The input value is invalid. Please try again: ");
			blocksize = Integer.parseInt(console.readLine());
		}



		System.out.println("\nPlease input a set size (1, 2, 4 or 8 lines per set): ");
		int setsize = Integer.parseInt(console.readLine());

		while ((setsize != 1) && (setsize != 2) && (setsize != 4) && (setsize != 8)) {
			System.out.println("The input value is invalid. Please try again: ");
			setsize = Integer.parseInt(console.readLine());
		}



		System.out.println("\nPlease input a replacement policy (0 for FIFO, 1 for LRU): ");
		int repl = Integer.parseInt(console.readLine());

		while ((repl != 0) && (repl != 1)) {
			System.out.println("The input value is invalid. Please try again: ");
			repl = Integer.parseInt(console.readLine());
		}

		boolean lru = false;
		if (repl == 1)
			lru = true;

		c = new Cache(setsize, lru);

		System.out.println("\n\nYour cache memory has been successfully built. These are your settings:\n\n"
				+ "> Writing method: Write-back\n"
				+ "> Word size: " + wordsize + " bytes\n"
				+ "> Block size: " + blocksize + " bytes");

		switch(setsize) {
		case 1:
			System.out.println("> Set size: " + setsize + " (Direct-mapping)");
			break;
		case 2:
			System.out.println("> Set size: " + setsize + " (Set associative)");
			break;
		case 4:
			System.out.println("> Set size: " + setsize + " (Set associative)");
			break;
		case 8:
			System.out.println("> Set size: " + setsize + " (Fully associative)");
			break;
		}

		if (!lru)
			System.out.println("> Replacement policy: FIFO \n\n");
		else
			System.out.println("> Replacement policy: LRU \n\n");

		c.displayCache();




		int memaddress = 0;
		int op = 0;
		int hitcount = 0;
		int accesscount = 0;
		int accesstime = 0;


		System.out.println("\n\n\nInput a main memory address (-1 to exit): ");
		memaddress = Integer.parseInt(console.readLine());

		while(memaddress != -1) {
			System.out.println("What would you like to do? (0 to LOAD, 1 to STORE): ");
			op = Integer.parseInt(console.readLine());


			Address adr = new Address(memaddress, wordsize, blocksize, setsize);

			if ((op == 0) || (op == 1)) {
				System.out.println("\n\n");

				c.modifyCache(adr, op);

				adr.displayAddress();
				c.displayHit(adr);

				accesscount++;
				if (adr.isHit())
					hitcount++;

				int cycles = adr.calcTime(blocksize, wordsize);
				accesstime += cycles;

				System.out.println("\nAccess time: " + cycles + " cycles");

				System.out.println("\n");

				c.displayCache();
				
			} else
				System.out.println("The operation input is invalid. Please try again.");

			System.out.println("\n\n\nInput a main memory byte address (-1 to exit): ");
			memaddress = Integer.parseInt(console.readLine());
		}

		System.out.println("\nExiting simulator... " + "\n=-=-=-=-=-=-=-=-=-=-=-=-=-=");

		float hitrate = (float) hitcount / (float) accesscount * 100;


		System.out.println("\nTotal of accesses to memory: " + accesscount);
		System.out.println("Hitrate: " + hitrate + "%");
		System.out.println("Total access time: " + accesstime + " cycles");
		System.out.println("\n\n=-=-=-=-=-=-=-=-=-=-=-=-=-="
				+ "\n Thank you for testing! :)");
	}
}

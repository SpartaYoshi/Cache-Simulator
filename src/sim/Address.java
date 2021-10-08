package sim;

public class Address {
	private int memaddress;
	private int memblock;
	private int cmblock;
	private int tag;
	private int word;
	private int set;
	private boolean hit;
	private boolean writeback;
	
	
	private int tcm;
	private int tmm;
	private int tbuf;
	private int tbt;
	private int time;
	
	
	
	
	public int getmemAddress() {
		return memaddress;
	}
	public int getmemBlock() {
		return memblock;
	}
	public int getcmBlock() {
		return cmblock;
	}
	public int getTag() {
		return tag;
	}
	public int getWord() {
		return word;
	}
	public int getSet() {
		return set;
	}
	public boolean isWriteback() {
		return writeback;
	}
	public boolean isHit() {
		return hit;
	}
	
	
	public void setWriteback(boolean writeback) {
		this.writeback = writeback;
	}
	public void setSet(int set) {
		this.set = set;
	}
	public void setmemAddress(int memaddress) {
		this.memaddress = memaddress;
	}
	public void setmemBlock(int memblock) {
		this.memblock = memblock;
	}
	public void setcmBlock(int cmblock) {
		this.cmblock = cmblock;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public void setWord(int word) {
		this.word = word;
	}
	public void setHit(boolean hit) {
		this.hit = hit;
	}
	
	
	
	public Address(int memaddress, int wordsize, int blocksize, int setsize) {
		this.memaddress = memaddress;
		hit = false;
		writeback = true;
		
		word = memaddress / wordsize;
		memblock = word / (blocksize/wordsize);
		
		tcm = 2;
		tmm = 21;
		tbuf = 1;
		
		
		
		switch(setsize) {
		case 1:										// direct-mapped cache
			cmblock = memblock % 8;		// cache size = 8 blocks
			set = cmblock;
			tag = memblock / 8;
			break;
			
		case 2:										// set associative cache
		case 4: 							
			// cmblock = setsize * set + <iteration of loop> 									//  (assigned automatically to an empty line)
			set = memblock % (8 / setsize);
			tag = memblock / (8 / setsize);
			break;
			
		case 8:										// fully associative cache
			// cmblock = setsize * set + <iteration of loop> 									//  (assigned automatically to an empty line)
			set = 0;
			tag = memblock;
			break;
			
		}
	}
	
	
	public void displayAddress() {
			System.out.println("\n> Address: " + memaddress + "   —   Word: " + word + "   —   Block: " + memblock);
			System.out.println("> Set: " + set + "   —   Tag: " + tag);
	}

	
	
	public int calcTime(int blocksize, int wordsize) {
		tbt = tmm + (blocksize/wordsize - 1) * tbuf;
		
		if (hit)
			time = tcm;
		else
			if (!writeback)
				time = tcm + tbt;
			else
				time = tcm + 2*tbt;
		return time;
	}



}

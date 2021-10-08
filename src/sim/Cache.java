package sim;

public class Cache {

	private final int setsize;
	private final boolean lru;  // 0 for FIFO, 1 for LRU


	private int[][] matrix;

	
	// CLASS CONSTRUCTOR
	
	public Cache(int setsize, boolean lru) {
		this.setsize = setsize;
		this.lru = lru;
		
		initMatrix();
	}
	
	
	// CACHE MEMORY INITIALIZER
	
	public void initMatrix() {
		matrix = new int[8][5];
		for (int i=0; i<8; i++)
			for (int j=0; j<5; j++)
				matrix[i][j] = 0;
	}
	
	
	
	
	
	
	// CACHE MEMORY DISPLAYER
	
	public void displayCache() {
		System.out.println(" busy dirty tag  repl. ||  data   \n"
						 + "------------------------------------");
		
		for (int i = 0; i < 8; i++) {
			int tagspacing = 1;
		
			switch(i) {
			case 2:
				if (setsize == 2)
					System.out.println("--------------------------------");
				break;
			case 4:
				if ((setsize == 2) || (setsize == 4))
					System.out.println("--------------------------------");
				break;
			case 6:
				if (setsize == 2)
					System.out.println("--------------------------------");
				break;
			}
			
			
			if (matrix[i][2] / 10 != 0) {
				tagspacing++;
				if (matrix[i][2] / 100 != 0) {
					tagspacing++;
				}
			}
			

			if (setsize == 1) {
				switch(tagspacing) {
				case 3:
					System.out.println("   " + matrix[i][0] + "     " + matrix[i][1] + "  " + matrix[i][2] + "    —   ||   b" + matrix[i][4]);
					break;
				case 2:
					System.out.println("   " + matrix[i][0] + "     " + matrix[i][1] + "   " + matrix[i][2] + "    —   ||   b" + matrix[i][4]);
					break;
				default: 
					System.out.println("   " + matrix[i][0] + "     " + matrix[i][1] + "    " + matrix[i][2] + "    —   ||   b" + matrix[i][4]);
					break;
				}
			}
			else {
				switch(tagspacing) {
				case 3:
					System.out.println("   " + matrix[i][0] + "     " + matrix[i][1] + "  " + matrix[i][2] + "    " + matrix[i][3] + "   ||   b" + matrix[i][4]);
					break;
				case 2:
					System.out.println("   " + matrix[i][0] + "     " + matrix[i][1] + "   " + matrix[i][2] + "    " + matrix[i][3] + "   ||   b" + matrix[i][4]);
					break;
				default:
					System.out.println("   " + matrix[i][0] + "     " + matrix[i][1] + "    " + matrix[i][2] + "    " + matrix[i][3] + "   ||   b" + matrix[i][4]);
					break;
				}
			}


			if (i == 7) 
				System.out.println("--------------------------------");
		}	
	}
	
	
	
	
	
	// GETTERS AND SETTERS FOR BUSY/DIRTY BIT
	
	public int getBusy(int cmblock) {
		return matrix[cmblock][0];
	}
	
	public void switchBusy(int cmblock, int n) {
			matrix[cmblock][0] = n;
	}
	
	
	public int getDirty(int cmblock) {
		return matrix[cmblock][1];
	}
	public void switchDirty(int cmblock, int bit) {
		matrix[cmblock][1] = bit;
	}
	
	
	
	
	
	// OTHER GETTERS AND SETTERS
	
	public int getcmTag(int cmblock) {
		return matrix[cmblock][2];
	}
	
	public void setcmTag(int cmblock, int tag) {
		matrix[cmblock][2] = tag;
	}
	
	public int getcmData(int cmblock) {
		return matrix[cmblock][4];
	}
	
	public void setcmData(int cmblock, int block) {
		matrix[cmblock][4] = block;
	}
	
	
	
	// CACHE DATA HIT DISPLAYER
	
	public void displayHit(Address adr) {
		if (!adr.isHit())
			System.out.println("> Cache data search: MISS");
		else
			System.out.println("> Cache data search: HIT");
	}
	
	
	
	
	// REPLACEMENT POLICY
	public void updateRepl(Address adr) {
		
		int oldest = matrix[setsize*adr.getSet()][3];
		adr.setcmBlock(setsize*adr.getSet());
		
		for (int i = 0; i < setsize; i++)
			if (matrix[setsize*adr.getSet() + i][3] > oldest) {
				oldest = matrix[setsize*adr.getSet() + i][3];
				adr.setcmBlock(setsize*adr.getSet() + i);
			}
	}
	
	public void cycleRepl(int cmblock, boolean hit) {
		int prehit = matrix[cmblock][3] + 1;
		matrix[cmblock][3] = 1;

		for (int i = 0; i < 8; i++) 
			if (i != cmblock && (matrix[i][3] != 0)) {
				matrix[i][3]++;
				if (lru && hit && (matrix[i][3] >= prehit))
					matrix[i][3]--;
			}
	}
	

	

	
	
	
	
	
	// CACHE OPERATOR
	
	public void modifyCache(Address adr, int op) {
		
		// HIT
		
		if (setsize == 1) {															// for direct-mapped					
			if ((adr.getTag() == getcmTag(adr.getcmBlock())) && (getBusy(adr.getcmBlock()) == 1)) {	
				adr.setHit(true);
				if (op == 1)
					switchDirty(adr.getcmBlock(), 1);
			}
		}
	
		else {																		// for auto associated cache lines
			for (int i = 0; i < setsize; i++) {
				if ((adr.getTag() == getcmTag(setsize*adr.getSet() + i)) && (getBusy(setsize*adr.getSet() + i) == 1)) {
					adr.setcmBlock(setsize*adr.getSet() + i);								 // cache line re-assignation for reference
					adr.setHit(true);
					
					if (op == 1)
						switchDirty(adr.getcmBlock(), 1);
				}
			}
		}
		
		
		
		
		// MISS
		
		if (!adr.isHit()) {
			
			// EMPTY CACHE LINE ASSIGNATION
			switch (setsize) {
			case 2:												// set assoc. (4 sets of 2 lines)
			case 4:												// set assoc: (2 sets of 4 lines)
			case 8:												// fully assoc (1 entire set of 8 lines)
				boolean replaced = false;	
				int i = 0;
				while (!replaced && i < setsize) 
					if (getcmData(setsize*adr.getSet() + i) == 0) {  // (only iterates on the set where the address belongs to)
						replaced = true;
						adr.setcmBlock(setsize*adr.getSet() + i);
					}
					else i++;
				
				// ALL LINES OCCUPIED -> REPLACEMENT POLICIES
					// +  info below on cycleRepl()
				if (!replaced)
					updateRepl(adr);
				
				break;
			}
			// DIRTY BIT ON CACHE MISS -> RUN WRITEBACK PROMPT
			if (getDirty(adr.getcmBlock()) == 1)
				adr.setWriteback(true);
			
			if (op == 0)
				switchDirty(adr.getcmBlock(), 0);

			else if (op == 1)
				switchDirty(adr.getcmBlock(), 1);

		}

		// REPLACEMENT CYCLE CACHE LINE MODIFIER (IT WON'T CYCLE IF FIFO + HIT)
			// FIFO -> replaced for oldest one chronologically input (this is why we don't cycle if it's a hit)
			// LRU -> replaced for oldest one that hasn't been used (always updated to 1)
		if ((!lru && !adr.isHit()) || lru)
			cycleRepl(adr.getcmBlock(), adr.isHit());


		// OTHER CACHE LINE MODIFIERS
		switchBusy(adr.getcmBlock(), 1);
		setcmTag(adr.getcmBlock(),adr.getTag());
		setcmData(adr.getcmBlock(), adr.getmemBlock());	
	}

}


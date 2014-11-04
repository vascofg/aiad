package elements;

import exceptions.ContainerFullException;

public abstract class Container extends MapElement {
	
	public static int defaultCapacity = 20;

	int capacity, usedCapacity;
	
	public Container(int capacity) {
		this.capacity = capacity;
		this.usedCapacity = 0;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void emptyContainer() {
		this.usedCapacity = 0;
	}

	public void addToContainer(int ammount) throws ContainerFullException {
		if ((this.usedCapacity + ammount) > this.capacity)
			throw new ContainerFullException();
		else
			this.usedCapacity += ammount;
	}

	public boolean isEmpty() {
		return usedCapacity == 0;
	}
	
	public abstract boolean truckCompatible(Truck truck);
}

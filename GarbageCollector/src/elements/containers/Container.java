package elements.containers;

import elements.MapElement;
import elements.trucks.Truck;
import exceptions.ContainerFullException;

public abstract class Container extends MapElement {
	
	public static int defaultCapacity = 20;

	int capacity, usedCapacity;
	
	public Container(int x, int y, int capacity) {
		super(x,y);
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
	
	public int getUsedCapacity() {
		return usedCapacity;
	}
	
	public abstract boolean truckCompatible(Truck truck);
}

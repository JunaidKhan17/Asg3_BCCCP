package bcccp.carpark;

import java.util.ArrayList;
import java.util.List;

import bcccp.tickets.adhoc.IAdhocTicket;
import bcccp.tickets.adhoc.IAdhocTicketDAO;
import bcccp.tickets.season.ISeasonTicket;
import bcccp.tickets.season.ISeasonTicketDAO;

public class Carpark implements ICarpark {
	
	private List<ICarparkObserver> observers;
	private String carparkId;
	private int capacity;
	private int nParked;
	private IAdhocTicketDAO adhocTicketDAO;
	private ISeasonTicketDAO seasonTicketDAO;
	
	
	
	public Carpark(String name, int capacity, 
			IAdhocTicketDAO adhocTicketDAO, 
			ISeasonTicketDAO seasonTicketDAO) {
		
		this.validateName(name);
		this.validateCapacity(capacity);
		this.carparkId = name;
		this.capacity = capacity;
		observers = new ArrayList<>();
		this.adhocTicketDAO = adhocTicketDAO;
		this.seasonTicketDAO = seasonTicketDAO;
	}

	
	
	@Override
	public void register(ICarparkObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	
	
	@Override
	public void deregister(ICarparkObserver observer) {
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}
	
	private void validateName(String name) {
		if (name == null || name.trim().equals("")) {
			throw new RuntimeException("The name was not correctly provided");
		}
	}
	
	
	
	private void validateCapacity(int capacity) {
		if (capacity <= 0) {
			throw new RuntimeException("The capacity should be a non-zero positive number.");
		}
	}
	
	
	
	private void notifyObservers() {
		for (ICarparkObserver observer : observers) {
			observer.notifyCarparkEvent();
		}
	}

	
	
	@Override
	public String getName() {
		return carparkId;
	}
	
	
	
	@Override
	public boolean isFull() {
		return nParked + seasonTicketDAO.getNumberOfTickets() == capacity;
	}
	
	
	
	@Override
	public IAdhocTicket issueAdhocTicket() {
		return adhocTicketDAO.createTicket(carparkId);
	}
	
	
	@Override
	public IAdhocTicket getAdhocTicket(String barcode) {
		return adhocTicketDAO.findTicketByBarcode(barcode);
	}
	
	
		
	@Override
	public float calculateAddHocTicketCharge(long entryDateTime) {
		//TODO Implement charge logic
		return 3.0f;
	}

	
	
	@Override
	public boolean isSeasonTicketValid(String barcode) {		
		ISeasonTicket ticket = seasonTicketDAO.findTicketById(barcode);
		
		// TODO implement full validation logic
		return ticket != null;
	}

	
	
	@Override
	public void registerSeasonTicket(ISeasonTicket seasonTicket) {
		seasonTicketDAO.registerTicket(seasonTicket);		
	}



	@Override
	public void deregisterSeasonTicket(ISeasonTicket seasonTicket) {
		seasonTicketDAO.deregisterTicket(seasonTicket);		
	}

	
	
	@Override
	public void recordSeasonTicketEntry(String ticketId) {
		ISeasonTicket ticket = seasonTicketDAO.findTicketById(ticketId);
		if (ticket == null) throw new RuntimeException("recordSeasonTicketEntry: invalid ticketId - " + ticketId);
		
		seasonTicketDAO.recordTicketEntry(ticketId);
		log(ticket.toString());
	}

	
	
	private void log(String message) {
		System.out.println("Carpark : " + message);
	}



	@Override
	public void recordAdhocTicketEntry() {
		nParked++;
		
	}



	@Override
	public void recordAdhocTicketExit() {
		nParked--;
		notifyObservers();		
	}



	@Override
	public void recordSeasonTicketExit(String ticketId) {
		ISeasonTicket ticket = seasonTicketDAO.findTicketById(ticketId);
		if (ticket == null) throw new RuntimeException("recordSeasonTicketExit: invalid ticketId - " + ticketId);
		
		seasonTicketDAO.recordTicketExit(ticketId);
		log(ticket.toString());
	}



	@Override
	public boolean isSeasonTicketInUse(String ticketId) {
		ISeasonTicket ticket = seasonTicketDAO.findTicketById(ticketId);
		if (ticket == null) throw new RuntimeException("recordSeasonTicketExit: invalid ticketId - " + ticketId);
		
		return ticket.inUse();
	}

















}

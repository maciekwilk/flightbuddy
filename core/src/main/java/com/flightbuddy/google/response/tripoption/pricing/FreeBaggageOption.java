package com.flightbuddy.google.response.tripoption.pricing;

public class FreeBaggageOption {
	
	private String kind;
    private BagDescriptor[] bagDescriptor;
    private int kilos;
    private int kilosPerPiece;
    private int pieces;
    private int pounds;
    
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public BagDescriptor[] getBagDescriptor() {
		return bagDescriptor;
	}
	public void setBagDescriptor(BagDescriptor[] bagDescriptor) {
		this.bagDescriptor = bagDescriptor;
	}
	public int getKilos() {
		return kilos;
	}
	public void setKilos(int kilos) {
		this.kilos = kilos;
	}
	public int getKilosPerPiece() {
		return kilosPerPiece;
	}
	public void setKilosPerPiece(int kilosPerPiece) {
		this.kilosPerPiece = kilosPerPiece;
	}
	public int getPieces() {
		return pieces;
	}
	public void setPieces(int pieces) {
		this.pieces = pieces;
	}
	public int getPounds() {
		return pounds;
	}
	public void setPounds(int pounds) {
		this.pounds = pounds;
	}    
}

package spell.search.criteria;

public class MagicItemSearchCriteria {

	public final static int EQUAL = 0;
	public final static int GREATHER_THAN = 1;
	public final static int LEATHER_THAN = 2;
	public final static int NOT_EQUAL = 3;
	
	private String name = null;
	private String originalName = null;
	private int itemTypeId = -1;
	private int slotId = -1;
	private int price = 0;
	private int priceSign = GREATHER_THAN;
	private int sourceId = -1;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOriginalName() {
		return originalName;
	}
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	public int getItemTypeId() {
		return itemTypeId;
	}
	public void setItemTypeId(int itemTypeId) {
		this.itemTypeId = itemTypeId;
	}
	public int getSlotId() {
		return slotId;
	}
	public void setSlotId(int slotId) {
		this.slotId = slotId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getPriceSign() {
		return priceSign;
	}
	public void setPriceSign(int priceSign) {
		this.priceSign = priceSign;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	
}

package spell.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import spell.model.Source;
import spell.model.Component;
import spell.model.ItemType;
import spell.model.MagicItem;
import spell.model.PlayerClass;
import spell.model.School;
import spell.model.Slot;
import spell.model.Spell;
import spell.services.ServiceFactory;


public class Session {

	public final static int STATUS_OK = 0;
	public final static int STATUS_ERROR = 1;
	
	private Connection connection;
	private int status;
	private Vector<String> errorMessages;
	
	public void setStatus(int status) {
		this.status = status;
	}

	public Session(Connection connection) {
		this.connection = connection;
		this.status = Session.STATUS_OK;
		errorMessages = new Vector<String>();
	}

	public Connection getConnection() {
		return connection;
	}

	public void clearStatus() throws SQLException {
		this.status = Session.STATUS_OK;
		this.connection.setAutoCommit(true);
		errorMessages.clear();
	}

	public void beginTransaction() throws SQLException {
		clearStatus();
		this.connection.setAutoCommit(false);
	}
	
	public boolean save(Object obj) {
		if (obj instanceof Spell) {
			return ServiceFactory.getSpellService().save((Spell) obj, this);
		} else if (obj instanceof PlayerClass) {
			return ServiceFactory.getPlayerClassService().save((PlayerClass) obj, this);
		} else if (obj instanceof Component) {
			return ServiceFactory.getComponentService().save((Component) obj, this);
		} else if (obj instanceof School) {
			return ServiceFactory.getSchoolService().save((School) obj, this);
		} else if (obj instanceof MagicItem) {
			return ServiceFactory.getMagicItemService().save((MagicItem) obj, this);
		}else if (obj instanceof Slot) {
			return ServiceFactory.getSlotService().save((Slot) obj, this);
		}else if (obj instanceof ItemType) {
			return ServiceFactory.getItemTypeService().save((ItemType) obj, this);
		}else if (obj instanceof Source) {
			return ServiceFactory.getSourceService().save((Source) obj, this);
		}
		return false;
	}

	public String[] getErrorMessages() {
		String[] messages = new String[errorMessages.size()];
		errorMessages.toArray(messages);
		return messages;
	}

	public void addErrorMessage(String error) {
		errorMessages.addElement(error);
	}

	public boolean delete(Object obj) {
		if (obj instanceof Spell) {
			return ServiceFactory.getSpellService().delete((Spell) obj, this);
		} else if (obj instanceof PlayerClass) {
			return ServiceFactory.getPlayerClassService().delete((PlayerClass) obj, this);
		} else if (obj instanceof Component) {
			return ServiceFactory.getComponentService().delete((Component) obj, this);
		} else if (obj instanceof School) {
			return ServiceFactory.getSchoolService().delete((School) obj, this);
		} else if (obj instanceof MagicItem) {
			return ServiceFactory.getMagicItemService().delete((MagicItem) obj, this);
		}else if (obj instanceof Slot) {
			return ServiceFactory.getSlotService().delete((Slot) obj, this);
		}else if (obj instanceof ItemType) {
			return ServiceFactory.getItemTypeService().delete((ItemType) obj, this);
		}else if (obj instanceof Source) {
			return ServiceFactory.getSourceService().delete((Source) obj, this);
		}
		return false;
	}

	public void commit() throws SQLException {
		this.connection.commit();
		clearStatus();
	}

	public void rollback() {
		try {
			this.connection.rollback();
			clearStatus();
		} catch (Exception e) {}
	}

	public void close() {
		try {
			if (!this.connection.isClosed()) {
				if (!this.connection.getAutoCommit())
					this.connection.rollback();
				this.connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getStatus() {
		return status;
	}

}

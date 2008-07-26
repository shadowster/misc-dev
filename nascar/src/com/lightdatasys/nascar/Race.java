package com.lightdatasys.nascar;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import com.lightdatasys.nascar.event.PositionChangeEvent;
import com.lightdatasys.nascar.event.PositionChangeListener;
import com.lightdatasys.nascar.fantasy.FantasyPlayer;
import com.lightdatasys.nascar.fantasy.FantasyResult;
import com.lightdatasys.nascar.fantasy.FantasyStanding;

public class Race
{
	public enum Flag {GREEN, YELLOW, RED, WHITE, CHECKERED, PRE_RACE};
	
	
	private static AbstractMap<Integer,Race> racesById = new HashMap<Integer,Race>();
	
	
	private AbstractTableModel resultsTableModel;
	
	private int raceId;
	
	private Season season;
	private Track track;
	
	private String name;
	private String nascarComId;
	private Date date;
	
	private int currentLap;
	private int lapCount;
	private int time;
	
	private Flag flag;
	private int lastFlagChange;
	private int cautionCount;
	
	private int leadChangeCount;
	private int leaderCount;

	private AbstractMap<Integer,Result> resultsByFinish;
	private AbstractMap<String,Result> resultsByCarNo;
	private AbstractMap<Driver,Result> resultsByDriver;
	
	private AbstractMap<Integer,FantasyResult> fantasyResultsByFinish;
	private AbstractMap<FantasyPlayer,FantasyResult> fantasyResultsByPlayer;
	
	private ArrayList<PositionChangeListener> positionChangeListeners;
	private ArrayList<PositionChangeListener> fantasyPositionChangeListeners;

	private AbstractMap<Integer,Standing> standingsByDriver;
	private AbstractMap<Integer,FantasyStanding> standingsByPlayer;

	
	public Race()
	{
		positionChangeListeners = new ArrayList<PositionChangeListener>();
		fantasyPositionChangeListeners = new ArrayList<PositionChangeListener>();
		
		currentLap = 0;
		lapCount = 0;
		time = 0;
		
		flag = Flag.PRE_RACE;
		lastFlagChange = 0;
		cautionCount = 0;
		
		leadChangeCount = 0;
		leaderCount = 0;
	}
	
	
	public int getId()
	{
		return raceId;
	}
	
	public Season getSeason()
	{
		return season;
	}
	
	public Track getTrack()
	{
		return track;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getNascarComId()
	{
		return nascarComId;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public int getCurrentLap()
	{
		return currentLap;
	}
	
	public int getLapCount()
	{
		return lapCount;
	}
	
	public Flag getFlag()
	{
		return flag;
	}
	
	public int getLastFlagChange()
	{
		return lastFlagChange;
	}
	
	public int getCautionCount()
	{
		return cautionCount;
	}
	
	public int getLeadChangeCount()
	{
		return leadChangeCount;
	}
	
	public int getLeaderCount()
	{
		return leaderCount;
	}
	
	public AbstractMap<Integer,Result> getResults()
	{
		return resultsByFinish;
	}
	
	public AbstractMap<Integer,FantasyResult> getFantasyResults()
	{
		return fantasyResultsByFinish;
	}
	
	public Result getResultByFinish(int finish)
	{
		if(resultsByFinish.containsKey(finish))
		{
			return resultsByFinish.get(finish);
		}
		
		return null;
	}
	
	public Result getResultByCarNo(String carNo)
	{
		if(resultsByCarNo.containsKey(carNo))
		{
			return resultsByCarNo.get(carNo);
		}
		
		return null;
	}
	
	public Result getResultByDriver(Driver driver)
	{
		if(resultsByDriver.containsKey(driver))
		{
			return resultsByDriver.get(driver);
		}
		
		return null;
	}
	
	public AbstractMap<Integer,Standing> getStandings()
	{
		return standingsByDriver;
	}
	
	public AbstractMap<Integer,FantasyStanding> getFantasyStandings()
	{
		return standingsByPlayer;
	}
	

	
	public void setCurrentLap(int currentLap)
	{
		this.currentLap = currentLap;
	}
	
	public void setLapCount(int lapCount)
	{
		this.lapCount = lapCount;
	}
	
	public void setFlag(Flag flag)
	{
		this.flag = flag;
	}
	
	public void setLastFlagChange(int lastFlagChange)
	{
		this.lastFlagChange = lastFlagChange;
	}
	
	public void setCautionCount(int cautionCount)
	{
		this.cautionCount = cautionCount;
	}
	
	public void setLeadChangeCount(int leadChangeCount)
	{
		this.leadChangeCount = leadChangeCount;
	}
	
	public void setLeaderCount(int leaderCount)
	{
		this.leaderCount = leaderCount;
	}
	
	public void setFinish(String carNo, int finish)
	{
		if(resultsByCarNo.containsKey(carNo))
		{
			Result result = resultsByCarNo.get(carNo);
			
			for(PositionChangeListener listener : positionChangeListeners)
			{
				PositionChangeEvent ev = new PositionChangeEvent(carNo, result.getFinish(), finish);
				listener.positionChanged(ev);
			}
			
			resultsByFinish.put(finish, result);
		}
	}
	
	public void setFantasyFinish(FantasyPlayer player, int finish)
	{
		if(fantasyResultsByPlayer.containsKey(player))
		{
			FantasyResult result = fantasyResultsByPlayer.get(player);
			
			for(PositionChangeListener listener : fantasyPositionChangeListeners)
			{
				PositionChangeEvent ev = new PositionChangeEvent(player.toString(), result.getFinish(), finish);
				listener.positionChanged(ev);
			}
			
			fantasyResultsByFinish.put(finish, result);
		}
	}
	
	public void updateFantasyFinishPositions()
	{
		ArrayList<FantasyResult> sorted = new ArrayList<FantasyResult>();
		sorted.addAll(fantasyResultsByPlayer.values());
		Collections.sort(sorted);
		
		for(int i = 0; i < sorted.size(); i++)
		{
			sorted.get(i).setFinish(i + 1);
		}
	}

	
	public void addPositionChangeListener(PositionChangeListener listener)
	{
		if(!positionChangeListeners.contains(listener))
			positionChangeListeners.add(listener);
	}
	
	public void addFantasyPositionChangeListener(PositionChangeListener listener)
	{
		if(!fantasyPositionChangeListeners.contains(listener))
			fantasyPositionChangeListeners.add(listener);
	}
	
	
	/*public AbstractTableModel getTableModel()
	{
		if(resultsTableModel == null)
		{
			resultsTableModel = new AbstractTableModel()
			{
				private static final long serialVersionUID = 1L;

				public int getColumnCount()
				{
					return 7;
				}

				public int getRowCount()
				{
					return results.size();
				}

				public String getColumnName(int columnIndex)
				{
					String[] columnNames =
					{
						"Finish",
						"Start",
						"Car",
						"Driver",
						"Led Laps",
						"Led Most Laps",
						"Penalties"
					};

					if(columnIndex < columnNames.length)
						return columnNames[columnIndex];
					
					return "";
				}

				public Object getValueAt(int rowIndex, int columnIndex)
				{
					if(results.containsKey(rowIndex + 1))
					{
						Result r = results.get(rowIndex + 1);
						
						Object[] columns =
						{
							r.getFinish(),
							r.getStart(),
							r.getCar(),
							r.getDriver(),
							r.ledLaps(),
							r.ledMostLaps(),
							r.getPenalties()
						};

						if(columnIndex < columns.length)
							return columns[columnIndex];
					}
					
					return "";
				}
				
			    public Class<?> getColumnClass(int c) 
			    {
			    	if(getValueAt(0, c) != null)
			    		return getValueAt(0, c).getClass();
			    	else
			    		return "".getClass();
			    }
			};
		}
		
		return resultsTableModel;
	}*/
	
	public void loadResults()
	{
		try
		{
			resultsByFinish = new HashMap<Integer,Result>();
			resultsByCarNo = new HashMap<String,Result>();
			resultsByDriver = new HashMap<Driver,Result>();
			
			Statement sResults = NASCARData.getSQLConnection().createStatement();
			sResults.execute("SELECT resultId, driverId, car, start, finish, ledLaps, ledMostLaps, penalties FROM nascarResult WHERE raceId=" + raceId
				+ " ORDER BY finish ASC");
			
			ResultSet rsResults = sResults.getResultSet();
			
			while(rsResults.next())
			{
				Driver driver = Driver.getById(rsResults.getInt("driverId"));
				String car = rsResults.getString("car");
				int start = rsResults.getInt("start");
				int finish = rsResults.getInt("finish");
				boolean ledLaps = (rsResults.getInt("ledLaps") == 0) ? false : true;
				boolean ledMostLaps = (rsResults.getInt("ledMostLaps") == 0) ? false : true;
				int penalties = rsResults.getInt("penalties");
				
				Result result = new Result(this, driver, car, start, finish, ledLaps, ledMostLaps, penalties);
				resultsByFinish.put(finish, result);
				resultsByCarNo.put(car, result);
				resultsByDriver.put(driver, result);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadFantasyResults()
	{
		try
		{
			fantasyResultsByFinish = new HashMap<Integer,FantasyResult>();
			fantasyResultsByPlayer = new HashMap<FantasyPlayer,FantasyResult>();
			
			Statement sPicks = NASCARData.getSQLConnection().createStatement();
			sPicks.execute("SELECT raceId, userId, driverId FROM nascarFantPick WHERE raceId=" + raceId);
			
			ResultSet rsPicks = sPicks.getResultSet();
			
			while(rsPicks.next())
			{
				FantasyPlayer player = FantasyPlayer.getByUserId(rsPicks.getInt("userId"));
				
				FantasyResult result;
				if(fantasyResultsByPlayer.containsKey(player))
				{
					result = fantasyResultsByPlayer.get(player);
				}
				else
				{
					result = new FantasyResult(this, player, 0);
					fantasyResultsByPlayer.put(player, result);
				}
				
				result.addDriver(Driver.getById(rsPicks.getInt("driverId")));
			}
			
			updateFantasyFinishPositions();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static Race getById(int raceId)
	{
		if(racesById.containsKey(raceId))
			return racesById.get(raceId);
		else
			return loadFromDatabase(NASCARData.getSQLConnection(), raceId);		
	}
	
	public static Race loadFromDatabase(Connection conn, int raceId)
	{
		Race race = new Race();
		
		try
		{
			Statement sRace = conn.createStatement();
			sRace.execute("SELECT raceId, trackId, seasonId, name, nascarComId, date FROM nascarRace WHERE raceId=" + raceId);
			
			ResultSet rsRace = sRace.getResultSet();
		
			if(rsRace.next())
			{
				racesById.put(raceId, race);
				
				race.raceId = raceId;
				
				race.season = Season.getById(rsRace.getInt("seasonId"));
				race.track = Track.getById(rsRace.getInt("trackId"));
				
				race.name = rsRace.getString("name");
				race.nascarComId = rsRace.getString("nascarComId");
				race.date = rsRace.getDate("date");
				
				race.loadResults();
				race.loadFantasyResults();
				
				race.standingsByDriver = Driver.getStandings(race);
				race.standingsByPlayer = FantasyPlayer.getStandings(race);
				
				return race;
			}
			else
			{
				System.out.println("Unknown raceId: " + raceId);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public String toString()
	{
		return getName();
	}
}

package com.lightdatasys.nascar.live.gui.cell;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import com.lightdatasys.gui.FontUtility;

public class FantasyPlayerCell extends Cell 
{
	private String label;
	
	private Color border;
	private Color background;
	private Color text;
	
	private boolean roundRectangles;
	
	private Font font;
	
	
	public FantasyPlayerCell(GraphicsDevice gd, int w, int h, String label, Color text, Color bg, Color border)
	{
		this(gd, w, h, label, text, bg, border, true);
	}
	
	public FantasyPlayerCell(GraphicsDevice gd, int w, int h, String label, Color text, Color bg, Color border, boolean roundRectangles)
	{
		super(gd, w, h);
		
		String[] temp = label.split(" ");
		label = temp[0].substring(0, 1) + temp[0].substring(temp[0].length() - 1, temp[0].length());
        
		
		this.label = label;
		this.border = border;
		this.background = bg;
		this.text = text;
		
		this.roundRectangles = roundRectangles;
		

		int borderWidth = 2;
		
		Graphics2D g = (Graphics2D)getImage().getGraphics();

        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        font = new Font(null, Font.BOLD, (int)((getHeight() - 10) * dpi / 72f));
		font = FontUtility.getScaledFont(getWidth() - 10, getHeight() - 10, label, font, g);
	}
	
	
	public void render(Graphics2D g)
	{
		int borderWidth = 2;
        
        FontMetrics metrics = g.getFontMetrics(font);

		float xOffset = (float)(getWidth() - font.getStringBounds(label, g.getFontRenderContext()).getWidth()) / 2.0f;
		float yOffset = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2.0f;

        //*
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //*/

        //*
        g.setFont(font);
        
		// border
		g.setColor(border);
		if(roundRectangles)
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);//, getWidth()/3, getWidth()/3);
		else
			g.fillRect(0, 0, getWidth(), getHeight());//, getWidth()/4, getWidth()/4);

		g.setColor(background);
		if(roundRectangles)
			g.fillRoundRect(borderWidth, borderWidth, getWidth()-2*borderWidth, getHeight()-2*borderWidth, 25, 25);//, getWidth()/4, getWidth()/4);
		else
			g.fillRect(borderWidth, borderWidth, getWidth()-2*borderWidth, getHeight()-2*borderWidth);//, getWidth()/4, getWidth()/4);

		//g.setFont(g.getFont().deriveFont(36.0f).deriveFont(Font.BOLD));
		g.setColor(text);
		g.drawString(label, xOffset, yOffset);
		//*/
		
		updated = false;
	}
}

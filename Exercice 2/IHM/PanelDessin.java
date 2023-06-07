package graphe.ihm;

import graphe.Controleur;
import graphe.metier.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class PanelDessin extends JPanel {
	// Position
	private int margeGraphe;
	private int coef;
	private int maxX;
	private int maxY;

	private Point point1, point2;

	private Controleur ctrl;
	private Graphics2D g2;

	private Integer posX;
	private Integer posY;

	public PanelDessin(Controleur ctrl) 
	{
		// définition du panel
		this.ctrl = ctrl;
		this.setLayout(new BorderLayout());
		this.setOpaque(false);

		// activation des composants
		GereSouris gereSouris = new GereSouris();

		this.addMouseListener(gereSouris);
		this.addMouseMotionListener(gereSouris);

		this.maxX = this.maxY = 0;
		for (Point p : this.ctrl.getSommets()) 
		{
			if (p.getX() > this.maxX)
				this.maxX = p.getX() + 1; // pour éviter le /0
			if (p.getY() > this.maxY)
				this.maxY = p.getY() + 1; // pour éviter le /0
		}
	}

	public void paintComponent(Graphics g) 
	{
		int hauteur = this.ctrl.getHauteurIHM() - 90;
		int largeur = this.ctrl.getLargeurIHM() - 10;

		super.paintComponent(g);

		g2 = (Graphics2D) g;

		int coef1, coef2;

		coef1 = hauteur / (this.maxY);
		coef2 = largeur / (this.maxX);

		if (coef1 < coef2)
			this.coef = coef1;
		else
			this.coef = coef2;

		if (this.coef < 1)
			this.coef = 1;

		this.margeGraphe = 0;

		for (Arete a : this.ctrl.getAretes()) 
		{
			if (a.getCouleur() != null)
				g2.setColor(a.getCouleur());
			else
				g2.setColor(Color.GRAY);

			Point p1 = a.getPointDepart();
			Point p2 = a.getPointArrivee();
			g2.drawLine((p1.getX() * this.coef) + this.margeGraphe, (p1.getY() * this.coef) + this.margeGraphe,
					(p2.getX() * this.coef) + this.margeGraphe, (p2.getY() * this.coef) + this.margeGraphe);
		}

		for (Point p : this.ctrl.getSommets()) 
		{
			if (p == this.point1 || p == this.point2)
				g2.setColor(Color.BLACK);
			else
				g2.setColor(Color.GRAY);
			Ellipse2D.Double circle = new Ellipse2D.Double(p.getX() * this.coef + this.margeGraphe - 5,
					p.getY() * this.coef + this.margeGraphe - 5,
					10, 10);
			g2.fill(circle);
		}

	}

	private class GereSouris extends MouseAdapter 
	{
		int posX, posY;
		Point point1, point2;
		Controleur ctrl;

		public void mousePressed(MouseEvent e) 
		{
			this.ctrl = PanelDessin.this.ctrl; // raccourci inhumain de l'argumentation
			try 
			{
				this.posX = e.getX();
				this.posY = e.getY();

				Point p = this.ctrl.trouverPoint(this.posX * 1.0 / PanelDessin.this.coef,
						this.posY * 1.0 / PanelDessin.this.coef);

				if (p == null) {
					this.point1 = this.point2 = null;
				} else {
					this.point2 = this.point1;
					this.point1 = p;
				}

				if (this.point1 != null && this.point2 != null) {
					Arete a = this.ctrl.trouverArete(this.point1, this.point2);

					if (this.ctrl.estColoriable(this.ctrl.getId(a)))
						this.ctrl.setAreteSelectionne(a.toString());
					else
						this.point2 = null;

					/* System.out.println( this.arete.getCouleur() ); */
				}

				PanelDessin.this.repaint();
				PanelDessin.this.point1 = this.point1;
				PanelDessin.this.point2 = this.point2;
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
			}
		}
	}

	public int getCoef() 
	{
		return this.coef;
	}
}
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

public class ButtonLayOut {
	
	public JButton initButton(JLabel mainLabel, int x, int y , String iconString){

	
		ImageIcon icon = new ImageIcon(iconString);
		final ImageIcon new_icon = ChangeImgSize(icon, 60, 60);

		JButton btn = new JButton(new_icon);

		btn.setPreferredSize(new Dimension(150, 60));


		btn.setVisible(true);

		return btn;
	}
	
	
	
	public static ImageIcon ChangeImgSize(ImageIcon img, int w, int h){

		Image image = img.getImage(); 
		Image newimg = image.getScaledInstance(w, h,  java.awt.Image.SCALE_SMOOTH);
		img = new ImageIcon(newimg); 

	    return img;
	}


}

package com.pku.judgeonline.common;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

public class Code extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int WIDTH = 70;
	private static int HEIGHT = 25;
	private static int LENGTH = 4;

	public Code()
	{
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession httpsession = request.getSession();
		response.setContentType("image/jpeg");
		ServletOutputStream servletoutputstream = response.getOutputStream();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		BufferedImage bufferedimage = new BufferedImage(WIDTH, HEIGHT, 1);
		Graphics g = bufferedimage.getGraphics();
		char ac[] = generateCheckCode();
		drawBackground(g);
		drawRands(g, ac);
		g.dispose();
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		ImageIO.write(bufferedimage, "JPEG", bytearrayoutputstream);
		byte abyte0[] = bytearrayoutputstream.toByteArray();
		response.setContentLength(abyte0.length);
		servletoutputstream.write(abyte0);
		bytearrayoutputstream.close();
		servletoutputstream.close();
		httpsession.setAttribute("check_code", new String(ac));
	}

	private static char[] generateCheckCode()
	{
		String s = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		char ac[] = new char[LENGTH];
		for (int i = 0; i < LENGTH; i++)
		{
			int j = (int) (Math.random() * 36D);
			ac[i] = s.charAt(j);
		}

		return ac;
	}

	private void drawRands(Graphics g, char ac[])
	{
		g.setColor(Color.BLACK);
		g.setFont(new Font(null, 3, 18));
		Random random = new Random();
		g.drawString((new StringBuilder()).append("").append(ac[0]).toString(), 1, random.nextInt(4) + 17);
		g.drawString((new StringBuilder()).append("").append(ac[1]).toString(), 19, random.nextInt(4) + 17);
		g.drawString((new StringBuilder()).append("").append(ac[2]).toString(), 39, random.nextInt(4) + 17);
		g.drawString((new StringBuilder()).append("").append(ac[3]).toString(), 57, random.nextInt(4) + 17);
		System.out.println(ac);
	}

	private void drawBackground(Graphics g)
	{
		Random random = new Random();
		g.setColor(new Color(0xdcdcdc));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		for (int i = 0; i < 120; i++)
		{
			int k = (int) (Math.random() * (double) WIDTH);
			int i1 = (int) (Math.random() * (double) HEIGHT);
			int k1 = (int) (Math.random() * 255D);
			int i2 = (int) (Math.random() * 255D);
			int k2 = (int) (Math.random() * 255D);
			g.setColor(new Color(k1, i2, k2));
			g.drawOval(k, i1, 1, 0);
		}

		for (int j = 0; j < 111; j++)
		{
			int l = (int) (Math.random() * 255D);
			int j1 = (int) (Math.random() * 255D);
			int l1 = (int) (Math.random() * 255D);
			g.setColor(new Color(l, j1, l1));
			int j2 = random.nextInt(WIDTH);
			int l2 = random.nextInt(HEIGHT);
			int i3 = random.nextInt(12);
			int j3 = random.nextInt(12);
			g.drawLine(j2, l2, j2 + i3, l2 + j3);
		}

	}

	public static void main(String args[])
	{
		char ac[] = generateCheckCode();
		System.out.println(ac);
	}

}

package com.pku.judgeonline.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.UserModel;

public class OnlineListener implements ServletContextListener, ServletContextAttributeListener, HttpSessionListener, HttpSessionAttributeListener, ServletRequestListener
{

	int counter = 0;
	HttpServletRequest request;
	private static HashMap<String, HttpSession> map = new HashMap<String, HttpSession>();
	
	public OnlineListener()
	{
		request = null;
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement;
			preparedstatement = connection.prepareStatement("delete from sessions where session_expires <= UNIX_TIMESTAMP()");
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void contextInitialized(ServletContextEvent servletcontextevent)
	{
		servletcontextevent.getServletContext();
	}

	public void contextDestroyed(ServletContextEvent servletcontextevent)
	{
	}

	public void attributeAdded(ServletContextAttributeEvent servletcontextattributeevent)
	{
		// System.out.println("new ServletContext session: " +
		// servletcontextattributeevent.getValue());
	}

	public void attributeRemoved(ServletContextAttributeEvent servletcontextattributeevent)
	{
	}

	public void attributeReplaced(ServletContextAttributeEvent servletcontextattributeevent)
	{
	}

	public void sessionCreated(HttpSessionEvent httpsessionevent)
	{
		/*counter = Integer.parseInt((String) application.getAttribute("userCounter"));
		counter++;
		application.setAttribute("userCounter", Integer.toString(counter));*/
		
		//String ip = request.getRemoteAddr();
		String ip = getRemortIP(request);
		String agent = request.getHeader("User-Agent");
		//System.out.println("User-Agent:\n"+agent);
		String id = httpsessionevent.getSession().getId();
		long session_expires = httpsessionevent.getSession().getCreationTime()/1000 + httpsessionevent.getSession().getMaxInactiveInterval();
		map.put(id, httpsessionevent.getSession());
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement;
			preparedstatement = connection.prepareStatement("insert into sessions (session_id, ip_address, user_agent, last_activity, session_expires) values(?, ?, ?, UNIX_TIMESTAMP(), ?)");
			preparedstatement.setString(1, id);
			preparedstatement.setString(2, ip);
			preparedstatement.setString(3, agent);
			preparedstatement.setLong(4, session_expires);
			preparedstatement.executeUpdate();
			connection.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void sessionDestroyed(HttpSessionEvent httpsessionevent)
	{
		//String s = (String) httpsessionevent.getSession().getAttribute("username");
		String id = httpsessionevent.getSession().getId();
		/*if(s != null)
		{
			Set<String> set = (Set<String>) application.getAttribute("online");
			set.remove(s);
			application.setAttribute("online", set);
		}*/
		map.remove(id);
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement;
			preparedstatement = connection.prepareStatement("delete from sessions where session_id=?");
			preparedstatement.setString(1, id);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		/*counter = Integer.parseInt((String) application.getAttribute("userCounter"));
		counter--;
		if (counter < 0)
			counter = 0;
		application.setAttribute("userCounter", Integer.toString(counter));*/
		map.remove(id);
		//System.out.println("Destroyed session: "+id);
	}

	public void attributeAdded(HttpSessionBindingEvent httpsessionbindingevent)
	{
		
		String s = "";
		try
		{
			Object Added = httpsessionbindingevent.getValue();
            if (Added.getClass().getName().equals("com.pku.judgeonline.common.UserModel"))
            	s = ((UserModel) httpsessionbindingevent.getSession().getAttribute("SESSION_USER_TAG")).getUser_id();
		}catch(Exception e)
		{
			s = null;
		}
		if (s != null && s != "")
		{
			Connection connection = DBConfig.getConn();
			//String ip = getRemortIP(request);
			//String agent = request.getHeader("User-Agent");
			String id = httpsessionbindingevent.getSession().getId();
			try
			{
				PreparedStatement preparedstatement;
				ResultSet resultset;
				preparedstatement = connection.prepareStatement("select session_id, ip_address, user_agent, last_activity, session_expires from sessions where user_id=?");
				preparedstatement.setString(1, s);
				resultset = preparedstatement.executeQuery();
				while(resultset.next())
				{
					String session_id = resultset.getString("session_id");
					//String ip_address = resultset.getString("ip_address");
					HttpSession session = map.get(session_id);
					if (session != null)
						session.invalidate();
					//String title = s + " login repeatedly";
					//String content = "Old  IP: " + ip_address + "\nNew IP: " + ip + "\n";
					//Tool.sendMail(connection, "System", s, title, "此消息也会发给管理员，请不要在比赛过程中重复登录。\n如果非本人登录，请修改密码以保证安全。\n"+content, 0);
					//content += "Old  Agent: "+resultset.getString("user_agent")+"\nNew Agent: "+agent;
					//Tool.sendMail(connection, "System", "root;power721", title, content, 0);
				}
				preparedstatement.close();
				resultset.close();
				/*Set<String> set = (Set<String>) application.getAttribute("online");
				set.add(s);
				application.setAttribute("online", set);*/
				
				preparedstatement = connection.prepareStatement("update sessions set user_id=? where session_id=?");
				preparedstatement.setString(1, s);
				preparedstatement.setString(2, id);
				preparedstatement.executeUpdate();
				preparedstatement.close();
				connection.close();
			} catch (SQLException e)
			{
				System.out.println("attributeAdded SQLException");
				e.printStackTrace();
			}
		}
	}

	public void attributeRemoved(HttpSessionBindingEvent httpsessionbindingevent)
	{
		//System.out.println("removed session: " + httpsessionbindingevent.getValue());
	}

	public void attributeReplaced(HttpSessionBindingEvent httpsessionbindingevent)
	{
		//System.out.println("replaced session: " + httpsessionbindingevent.getValue());
	}

	public void requestDestroyed(ServletRequestEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	public void requestInitialized(ServletRequestEvent event)
	{
		request = (HttpServletRequest)event.getServletRequest();
	}
	
	public String getRemortIP(HttpServletRequest request)
	{   
		if (request.getHeader("x-forwarded-for") == null)
		{   
			return request.getRemoteAddr();  
		}  
		return request.getHeader("x-forwarded-for");  
	}   
}

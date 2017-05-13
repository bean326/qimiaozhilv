package cc.lv1.rpg.gs.util;

import java.util.Random;

public class CreatePassword
{

	public static void main(String[] args)
	{
		StringBuffer password = new StringBuffer();
		Random rand = null;
		while (password.length() < 16)
		{
			rand = new Random();
			int x = rand.nextInt(127);

			if (x >= 33 && x <= 126)
				password.append((char) x);
			else
				continue;
		}
		System.out.println(password);

	}

}

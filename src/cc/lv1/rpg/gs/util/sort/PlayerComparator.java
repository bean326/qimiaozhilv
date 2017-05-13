package cc.lv1.rpg.gs.util.sort;

import java.util.Comparator;

public class PlayerComparator implements Comparator
{
	public int compare(Object o1, Object o2) 
	{
		Order c1 = (Order) o1;
		Order c2 = (Order) o2;
		if("level".equals(c1.getKey()) && "level".equals(c2.getKey()))
		{
			int type1 = c1.getPlayer().getZhuanshengState();
			int type2 = c2.getPlayer().getZhuanshengState();
			if(type1 < type2)
			{
				return 1;
			}
			else if(type1 > type2)
			{
				return -1;
			}
			else
			{	
				if (c1.getValue() < c2.getValue()) 
				{
					return 1;
				} 
				else if (c1.getValue() == c2.getValue()) 
				{
					return 0;
				}
				else 
				{
					return -1;
				}
			}
		}
		else
		{
			if (c1.getValue() < c2.getValue()) 
			{
				return 1;
			} 
			else if (c1.getValue() == c2.getValue()) 
			{
				return 0;
			}
			else 
			{
				return -1;
			}
		}
	}
	
}




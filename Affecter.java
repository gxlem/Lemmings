public interface Affecter{
	/*этот интерфейс позволяет некоторым типам леммингов влиять на карту и/или удалять их модификации*/
	
	//повлиять на карту
	public void affectMap();
	
	//сбросить карту
	public void resetMap();

}

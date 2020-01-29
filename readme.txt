INSTALACJA
	Należy zainstalować IntelliJ i uruchomić w nim pliki projektu - wymaga on konsoli do obsługi.
URUCHOMIENIE
	Serwer:	Jako pierwszy parametr należy podać port TCP na którym serwer będzie nasłuchiwał połączeń od klientów.
			Jako drugi parametr należy podać port UTP na którym będzie rozsylany broadcast dla widzów.
	Klient:	Jako pierwszy parametr należy podać adres IP serwera.
			Jako drugi parametr należy podać port serwera.
	Widz:	Jako pierwszy parametr należy podać port UDP na którym widz będzie nasłuchiwał broadcastu.
UŻYWANIE
	Serwer:	Uruchomiony obsługuje gry.
	Klient:	LIST - drukuje listę połączonych graczy.
			PLAY - rozpoczyna oczekiwanie na drugiego gracza, a następnie na grę
			LOGOUT - wylogowuje gracza z serwera
	Widz:	Uruchomiony pokazuje stany wszystkich planszy.
IMPLEMENTACJA
	Serwer:
		Main
			Główna klasa programu, zajmuje się obsługą błędów.
			Metody:
				public static void main(String[] args)
					Uruchamia GamesControllera i obsługuje błędy
		GamesController
			Uruchamia sockety, akceptuje połączenia od klientów i dobiera przeciwnika klientowi
			Metody:
				public GamesController(int s,int u) throws IOException
					Inicjalizuje wszystkie pola i uruchamia sockety
				public void start()
					Akceptuje nowych graczy i uruchamia wątki ich obsługujące.
				public void deleteClient(int id)
					Usuwa gracza z bazy graczy
				public Collection<ClientController> getClientsList()
					Zwraca listę graczy
				public ClientController startGame(ClientController sck)
					Jeśli to gracz 1, to rozpoczyna oczekiwanie, jeśli to gracz 2 to zwraca mu gracza 1 do wspólnej gry.
				public static int getPort()
					Zwraca port UDP
		ClientController
			Komunikuje się z klientem i rozpoczyna jego gry.
			Metody:
				public ClientController(GamesController gc, Socket sck, int id)
					Inicjalizuje pola klienta.
				@Override public void run()
					Nasłuchuje poleceń od klienta i i rozpoczyna odpowiednie akcje.
				private void startGame() throws IOException
					Rozpoczyna grę, jeśli jest dostępny rywal.
					Jeśli nie ma rywala to czeka na niego.
				private void sendClientList() 
					Wysyła klientowi listę graczy oddzieloną tabulatorami.
				private void killClient()
					Usuwa gracza z bazy graczy.
				public String readDataFromClient() throws IOException
					Pobiera jedną linię danych od gracza.
				public void waitUntilGame()
					Czeka dopóki flaga się nie zmieni
				public void sendDataToClient(String data) throws IOException
					Wysyła data do klienta
				public void endWaiting()
					Zmienia flagę czekania na fałsz
				public String getClientinfo()
					Zwraca id klienta, jego adres i port odpowiednio sformatowane
		Game
			Obsługuje grę.
			Metody:
				public Game(ClientController clientController, ClientController rival)
					Inicjalizuje pola gry.
				public void run() throws IOException
					Przeprowadza grę, kończąc ją jednym z dwóch dostępnych wyników.
				private int checkForVictory()
					Przeszukuje planszę pod względem wygranych lub remisów
				private boolean checkPlayer(int vNumber)
					Przeszukuje pole gry pod względem jednego z oczekiwanych wyników
				private void sendGameInfoOnUDP() throws  IOException
					Rozsyła stan gry widzom.
				private String getViewerData()
					Zwraca sformatowane dane dla widzów
				private void sendInfoAboutTurns() throws  IOException
					Wysyła graczom informacje o następnej turze
				private void swapPlayer()
					Zmienia aktywnego gracza
				private String fieldInfo()
					Formatuje informacje o planszy
	Klient:
		Main
			Klasa główna programu - obsługuje błędy
			Metody:
				public static void main(String[] args)
					Sprawdza poprawność parametrów i uruchamia proces gracza
		Game
			Przeprowadza rozgrywkę
			Metody:
				public Game(InetAddress addr, int port) throws IOException
					Inicjalizuje gracza.
				private void printInitMessages()
					Wyświetla info o poleceniach.
				public void start() throws IOException
					Przyjmuje komendy od gracza i rozpoczyna odpowiednią akcję.
				private void startGame() throws IOException
					Rozpoczyna i przeprowadza grę.
				private void printField(String substring)
					Wypisuje planszę na ekranie wraz z informacjami o graczu
				private String numerToXO(char charAt,int i)
					Zamienia kod pola na X,O lub puste
				private void logout() throws IOException
					Wylogowuje gracza
				private void getPlayersList() throws IOException
					Wypisuje listę graczy oddzielonych tabulatorami
				private String getNextInputLine() throws IOException
					Pobiera linię danych od serwera
				private void sendMessage(String msg) throws IOException
					Wysyła dane do serwera
	Widz:
		Main
			Główna klasa programu, wyświetla odbierane gry
			Metody:
				public static void main(String[] args)
					Uruchamia socket i odbiera pakiety
				private static void printField(String substring)
					Wyświetla planszę gry i graczy którzy biorą udział
				private static String numerToXO(char charAt,int i)
					Zamienia kod pola na X, O lub puste
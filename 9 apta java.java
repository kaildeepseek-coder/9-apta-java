import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Қатысушы (жүгіруші) тапсырмасы. Runnable интерфейсін іске асырады.
class RunnerTask implements Runnable {
    private static final int TOTAL_STAGES = 5;

    private final String runnerName;
    private final List<String> finishOrder; // ортақ тізім: мәреге келу реті
    private final Random random = new Random();

    public RunnerTask(String runnerName, List<String> finishOrder) {
        this.runnerName = runnerName;
        this.finishOrder = finishOrder;
    }

    @Override
    public void run() {
        System.out.println(runnerName + " жарысты бастады (ағын: "
                + Thread.currentThread().getName() + ")");

        for (int stage = 1; stage <= TOTAL_STAGES; stage++) {
            try {
                // Әр кезеңде әртүрлі кідіріс: 200-ден 800 мс-қа дейін
                int delay = 200 + random.nextInt(600);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println(runnerName + " үзілді!");
                return;
            }
            System.out.println(runnerName + " " + stage + "-кезеңді өтті");
        }

        // Бір мезгілде бірнеше ағын тізімге жазбауы үшін synchronized
        synchronized (finishOrder) {
            finishOrder.add(runnerName);
            System.out.println(">>> " + runnerName + " мәреге жетті! Орны: "
                    + finishOrder.size());
        }
    }
}

public class SportsCompetition {
    public static void main(String[] args) {
        // 1) start() және run() айырмашылығын көрсету
        Runnable demoTask = () ->
                System.out.println("Демо: код орындалып жатқан ағын = "
                        + Thread.currentThread().getName());

        System.out.println("--- run() тікелей шақыру ---");
        new Thread(demoTask, "Demo-Thread").run();      // жаңа ағын ҚҰРЫЛМАЙДЫ, main ішінде орындалады

        System.out.println("--- start() шақыру ---");
        Thread demoThread = new Thread(demoTask, "Demo-Thread");
        demoThread.start();                              // жаңа ағын құрылады
        try {
            demoThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2) Негізгі жарыс
        System.out.println("\n=== Жарыс басталды! ===");
        List<String> finishOrder = new ArrayList<>();
        String[] names = {"Асқар", "Мадина", "Данияр", "Айгерім", "Ержан"};
        List<Thread> threads = new ArrayList<>();

        for (String name : names) {
            Thread runnerThread = new Thread(new RunnerTask(name, finishOrder), name);
            threads.add(runnerThread);
            runnerThread.start();
        }

        // 3) Барлық ағынның аяқталуын күту
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 4) Нәтиже
        System.out.println("\n=== Мәреге жету реті ===");
        for (int i = 0; i < finishOrder.size(); i++) {
            System.out.println((i + 1) + "-орын: " + finishOrder.get(i));
        }
        System.out.println("\nБарлық ағындардың жұмысы аяқталды. Жарыс біткен.");
    }
}
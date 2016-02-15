package separation.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import separation.objects.Snils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import separation.SeparationSnils;


/**
 * Created by VSKryukov on 15.01.2016.
 */


public class MainController implements Initializable {



    @FXML
    public TextField txtPathOutLoadListSnils;
    @FXML
    public TextField txtPathSnilsList;
    @FXML
    public TextField txtPathOutDir;
    @FXML
    public Label lblPathOutLoadListSnils;
    @FXML
    public Label lblPathSnilsList;
    @FXML
    public Label lblPathOutDir;
    @FXML
    public TextArea editTextArea;
    @FXML
    public ProgressIndicator prBarReadData;
    @FXML
    public ProgressIndicator prBarWork;
    @FXML
    public ProgressIndicator prBarWriteData;
    @FXML
    public Button btnRun;
    @FXML
    public Button btnReset;
    @FXML
    public Label lblLoad1;
    @FXML
    public Label lblLoad2;
    @FXML
    public Label lblAllFile1;
    @FXML
    public Label lblAllFile2;



    private File fileOutloadListSnils;
    private File fileListSnils;
    private File dirOut;

    //Separation
    public String certStartDate;
    public String certEndDate;
    public Integer regionSortVrn = 36;
    public Integer regionSortMrm = 51;
    public Integer regionSortYNAO = 89;
    public Integer regionSortHab = 27;
    public Integer regionSortSah = 65;
    public Integer regionSortPerm = 59;
    public LinkedList<String> incorrectListSnils;
    public Map<Long, Snils> deleteList;
    public LinkedList<Long> expectedListFull;
    public LinkedList<String> wrangListSnilsSecondList;

    public ObservableList<String> debugLog = FXCollections.observableArrayList();
    public String reportPath;
    public String header;
    public double prWorkIndicator = 0.0F;
    public int deleteListSize;
    public int expectedListFullSize;
    public  Thread process;
    public boolean flagRunning = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        debugLog.addListener(new ListChangeListener<String>() {


            @Override
            public void onChanged(Change<? extends String> c) {

                while (c.next()) {
                    for (String strLog : c.getAddedSubList()) {
                        editTextArea.setText(editTextArea.getText() + "\n" + strLog);
                    }
                }
            }
        });
    }

        public void run(ActionEvent actionEvent) throws FileNotFoundException, InterruptedException {
        Object source = actionEvent.getSource();



        if (!(source instanceof Button)){
            return;
        }

        Button clickedButton = (Button) source;

        switch (clickedButton.getId()){
            case "btnRun":


                txtPathOutLoadListSnils.setText("c:\\1.txt");
                txtPathSnilsList.setText("c:\\2.txt");
                txtPathOutDir.setText("c:\\");
                resetProgressIndiactor();
                if (checkInputData()){
                    btnRun.setText("Стоп");
                    editTextArea.setText("");
                    Service process = createServiceReading(fileOutloadListSnils, fileListSnils);
                    prBarReadData.progressProperty().unbind();
                    prBarReadData.setProgress(-1.0F);
                    prBarReadData.progressProperty().bind(process.progressProperty());
                    process.start();

                    boolean t = false;
                    while (t){
if (process.isRunning()){
    btnRun.setText("YtНуы");
    System.out.println("YtНуы");
} else {
    btnRun.setText("Запуск");
    System.out.println("Запуск");
}
                       // btnRun.setText("Запуск");

                    }

                    System.out.println("OK");




                       //SeparationSnilsListRun();
                    //prBarWork.progressProperty().unbind();

                    }



               // editTextArea.setText("Trat tatata");
                //btnRun.setDisable(false);
               // btnRun.setText("Запуск");
                break;

            case "btnReset" :
                prBarReadData.setProgress(-1.0F);
                System.out.println(Thread.currentThread().toString());
                process.stop();
        }
    }


    private Service createServiceReading(File snilsOutload, File snilsList) {
        Service thread = new Service<Integer>() {
            @Override
            protected Task createTask() {
                return new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {
                        int i=0;
                        readUnloadSnilsList(snilsOutload);
                        i++;
                        long k=0;
                        updateProgress(i, 2);
                        while(k< 1000000000){
                            k++;
                        }
                        readUnloadSnils(snilsList);
                        i++;
                        updateProgress(i,2);

                        if (i < 2){
                            return i;
                        } else {
                            return i;
                        }
                    }

                };
            }
        };
        return thread;
    }



    private void SeparationSnilsListRun() throws FileNotFoundException, InterruptedException {
        editTextArea.setText("");
       // debugLog = new LinkedList<>();
       // prBarReadData.setVisible(true);

        Long start_time = System.currentTimeMillis();

        debugLog.add("----------------------- Began of process of reading outload of snils List ----------------------");
        //Platform.runLater(() -> addTextAreaLine("Начинаю процесс чтения данных из файла выгрузки СНИЛСов!"));
        addTextAreaLine("Начинаю процесс чтения данных из файла выгрузки СНИЛСов!");


        Platform.runLater(() ->prBarReadData.setProgress(0.0F));


        readUnloadSnilsList(fileOutloadListSnils);



                //readUnloadSnilsList(fileOutloadListSnils);

        debugLog.add("--------------------------- Process of data reading is ended -----------------------------------");
        addTextAreaLine("Процесс чтения данных окончен!");
        //Platform.runLater(() -> addTextAreaLine("Процесс чтения данных окончен!"));

        debugLog.add("------------------------- Began of process of reading  of snils --------------------------------");
        addTextAreaLine("Начинаю процесс чтения данных из файла-спаска СНИЛСов!");

        //prBarReadData.setProgress(0.5F);
        Platform.runLater(() -> prBarReadData.setProgress(0.5F));
        readUnloadSnils(fileListSnils);

        Platform.runLater(() -> prBarReadData.setProgress(1.0F));

        debugLog.add("--------------------------- Process of data reading is ended -----------------------------------");
        addTextAreaLine("Процесс чтения данных окончен!");
        //TimeUnit.SECONDS.sleep(5);

        //prBarWork.setVisible(true);
        debugLog.add("------------------------- Began process of separation of snils ---------------------------------");
        addTextAreaLine("Начинаю процесс сепарирования СНИЛСов!");

        generateReportDir(dirOut);

        separationListSnils();

        debugLog.add("------------------------- Process of separation of snils is ended ------------------------------");
        addTextAreaLine("Процесс сепарирования СНИЛСов окончен!");

    }

    private void generateReportDir(File dirOut){
        reportPath = dirOut.toString() + "\\Report_" + certStartDate + "_" + certEndDate;
        File reportDir = new File(reportPath);
        reportDir.mkdirs();
        reportPath = reportDir.getAbsolutePath() + "\\";

    }

    private void addTextAreaLine(String str){

        System.out.println(editTextArea.getText());
        Platform.runLater(() -> editTextArea.setText((editTextArea.getText() + str + "\n")));
        //editTextArea.setText((editTextArea.getText() + str + "\n"));
    }

    public boolean checkInputData(){
        fileOutloadListSnils = new File(txtPathOutLoadListSnils.getText());
        fileListSnils = new File(txtPathSnilsList.getText());
        dirOut = new File(txtPathOutDir.getText().replace("\\","//"));


        if (txtPathOutLoadListSnils.getText().isEmpty()) {
            DialogController.showInfoDialog("ВНИМАНИЕ!!!", "Не заполнено поле \"" + lblPathOutLoadListSnils.getText() + "\". \n" +
                    " Заполните поле.");
            return false;
        } else if (!(fileOutloadListSnils.exists() && fileOutloadListSnils.isFile())){

            DialogController.showErrorDialog("ВНИМАНИЕ!!!", "Указанный файл: \"" + txtPathOutLoadListSnils.getText() + "\" не найден или" +
                    " не является файлом. \n" +
                    "Убедитесь, что файл существует и повторите попытку.");
            return false;
        }

        if (txtPathSnilsList.getText().isEmpty()) {
            DialogController.showInfoDialog("ВНИМАНИЕ!!!", "Не заполнено поле \"" + lblPathSnilsList.getText() + "\". \n" +
                    " Заполните поле.");
            return false;
        } else if (!(fileListSnils.exists() && fileListSnils.isFile())){

            DialogController.showErrorDialog("ВНИМАНИЕ!!!", "Указанный файл: \"" + txtPathSnilsList.getText() + "\" не найден или" +
                    " не является файлом. \n" +
                    "Убедитесь, что файл существует и повторите попытку.");
            return false;
        }

        if (txtPathOutDir.getText().isEmpty()){
            DialogController.showInfoDialog("ВНИМАНИЕ!!!", "Не заполнено поле \"" + lblPathOutDir.getText() + "\". \n" +
                    " Заполните поле.");
            return false;
        } else if (!dirOut.exists()){
            DialogController.showInfoDialog("ВНИМАНИЕ!!!", "Указанный каталог: \"" + txtPathOutDir.getText() + "\" не найден.\n" +
                    "Указанный каталог будет создан.");

        }else if (dirOut.isFile()){
            DialogController.showErrorDialog("ВНИМАНИЕ!!!", "Указанный каталог: \"" + txtPathOutDir.getText() + "\" не является " +
                    "каталогом. \n" +
                    "Убедитесь, что файл существует и повторите попытку.");
            return false;
        }
        return true;
    }


    private void readUnloadSnilsList(File snilsOutload) throws FileNotFoundException {

        incorrectListSnils = new LinkedList<>();
        deleteList = new HashMap<>();
        LinkedList debugLogTmp = new LinkedList<>();

        Scanner scanner = new Scanner(snilsOutload, "UTF-8");

        //Вытаскиваем параметры фильтров
        String filterLine = scanner.nextLine();
        String [] parseFilterLine = filterLine.split(";");
        certStartDate = getDateFromStr(parseFilterLine[0]);
        certEndDate = getDateFromStr(parseFilterLine[1]);

        //запоминаем шапку таблицы
        header = scanner.nextLine();
        //игнорим параметры в шапке файла и пустую строку
        scanner.nextLine();

        int countStr = 0;
        //читаем данные файла
        while (scanner.hasNext()) {
            //System.out.println("!!!!!! - " + countStr);
            String line = scanner.nextLine();
            String[] parsedLine = line.split(",");
            //String[] parsedRegionLine = parsedLine[5].split(" ");

            try {
                String[] parsedRegionLine = parsedLine[parsedLine.length - 2].split(" ");
                //Проверяем, что нет незаполненного региона в списке 1
                Integer.parseInt(parsedRegionLine[0].replace("\"", ""));
                //Проверяем, что нет незаполненного снилса в списке 1
                Long.parseLong(parsedLine[parsedLine.length - 1].replace("\"", ""));
                Snils sb = new Snils(parsedLine);
                if (parsedLine.length < 7) {
                    incorrectListSnils.add(line);

                } else if ((Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortVrn) ||
                        (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortMrm) ||
                        (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortYNAO) ||
                        (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortHab) ||
                        (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortSah) ||
                        (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortPerm)) {

                    deleteList.put(Long.parseLong(parsedLine[parsedLine.length - 1].replace("\"", "")), new Snils(parsedLine));

                }
                countStr++;
                //System.out.println(countStr + " - " + Long.parseLong(parsedLine[6].replace("\"", "")) + line);
            } catch (Exception e) {
                System.out.println(e.toString());

                incorrectListSnils.add(line);

            }
        }
        scanner.close();
       // System.out.println("In file 1.txt it is found records - " + countStr);
       // debugLog.add("In file 1.txt it is found records - " + countStr);
        final int finalCountStr = countStr;
        try {
        //    Platform.runLater(() -> debugLog.push("In file 1.txt it is found records - " + finalCountStr));
        } catch (NullPointerException e){
          //  System.out.println(e);
        }

   //     Platform.runLater(() -> lblAllFile1.setText(lblAllFile1.getText() + finalCountStr));
      //  System.out.println("From 1.txt it is loaded records - " + deleteList.size());
        debugLog.add("From 1.txt it is loaded records - " + deleteList.size());
//        Platform.runLater(() -> debugLog.addAll(debugLogTmp));

      //  Platform.runLater(() -> lblLoad1.setText(lblLoad1.getText() + deleteList.size()));
        deleteListSize = deleteList.size();
    }

    private void readUnloadSnils(File snilsList) throws FileNotFoundException {

        expectedListFull = new LinkedList<>();
        wrangListSnilsSecondList = new LinkedList<>();

        Scanner scanner = new Scanner(snilsList, "UTF-8");
        scanner.nextLine(); //игнорируем шапку

        //читаем данные из файла
        while (scanner.hasNext()) {
            String line = scanner.nextLine();

            try {
                Long.parseLong(line.replace("\"", ""));
                expectedListFull.add(Long.parseLong(line.replace("\"", "")));
            } catch (Exception e) {
                wrangListSnilsSecondList.add(line);
            }


        }
        scanner.close();

      //  System.out.println("In file 2 it is found records - " + expectedListFull.size());
        //Platform.runLater(() -> debugLog.add("In file 2 it is found records - " + expectedListFull.size()));
        Platform.runLater(() -> lblAllFile2.setText(lblAllFile2.getText() + expectedListFull.size()));
      //  expectedListFull = DeleteDouble(expectedListFull);
      //  System.out.println("From 2.txt it is loaded records - " + expectedListFull.size());
       // Platform.runLater(() -> debugLog.add("From 2.txt it is loaded records - " + expectedListFull.size()));
        Platform.runLater(() -> lblLoad2.setText(lblLoad2.getText() + expectedListFull.size()));
        debugLog.add("From 2.txt it is loaded records - " + expectedListFull.size());
        expectedListFullSize = expectedListFull.size();
    }

    private void separationListSnils () {
        LinkedList<Snils> indexList = new LinkedList<>();
        LinkedList<Snils> indexListVRN = new LinkedList<>();
        LinkedList<Snils> indexListMrm = new LinkedList<>();
        LinkedList<Snils> indexListYNAO = new LinkedList<>();
        LinkedList<Snils> indexListHab = new LinkedList<>();
        LinkedList<Snils> indexListSah = new LinkedList<>();
        LinkedList<Snils> indexListPerm = new LinkedList<>();


        String strRegion = new StringBuilder()
                .append("Данные разделяем по сл регионам: ")
                .append(regionSortVrn).append(",")
                .append(regionSortMrm).append(",")
                .append(regionSortYNAO).append(",")
                .append(regionSortHab).append(",")
                .append(regionSortSah).append(",")
                .append(regionSortPerm).append(".")
                .toString();

        System.out.println(strRegion);
        debugLog.add(strRegion);
        addTextAreaLine(strRegion);



        System.out.println("expectedList.size() befor - " + expectedListFull.size());




        boolean flag = true;

        Platform.runLater(() -> prBarWork.setProgress(0.0F));
        //System.out.println("---------------------- Beginning process of separation of SNILS ------------------------------");
        int countDoneSnils = 0;

        for (Map.Entry<Long, Snils> deleteSnils : deleteList.entrySet()) {
            Long key = deleteSnils.getKey();
            Snils value = deleteSnils.getValue();

            Iterator<Long> iterator = expectedListFull.iterator();//получение итератора для списка

            while (iterator.hasNext())      //проверка, есть ли ещё элементы
            {
                //получение текущего элемента и переход на следующий
                Long expectedSnils = iterator.next();
                if (key.equals(expectedSnils)) {
                    System.out.println("Snils \"" + expectedSnils + "\" is found in the listUnloadSnils");
                    debugLog.add("Snils \"" + expectedSnils + "\" is found in the listUnloadSnils");

                    iterator.remove();
                    flag = false;
                }
            }

            if (flag) {
                indexList.add(value);
                String[] parsedRegionLine = value.getRegion().split(" ");
                if (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortVrn) {
                    indexListVRN.add(value);
                    System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list VRN");
                    debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list VRN");

                } else if (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortMrm) {
                    indexListMrm.add(value);
                    System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Mrm");
                    debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Mrm");
                } else if (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortYNAO) {
                    indexListYNAO.add(value);
                    System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list YNAO");
                    debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list YNAO");
                } else if (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortHab) {
                    indexListHab.add(value);
                    System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Hab");
                    debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Hab");
                } else if (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortSah) {
                    indexListSah.add(value);
                    System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Sah");
                    debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Sah");
                } else if (Integer.parseInt(parsedRegionLine[0].replace("\"", "")) == regionSortPerm) {
                    indexListPerm.add(value);
                    System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Perm");
                    debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list Perm");
                }
                System.out.println("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list");
                debugLog.add("SNILS \"" + value.getSnilsNumber() + "\" is added to the final list");
            } else {
                flag = true;
            }

            //ProgressIndicator change status
            //TimeUnit.SECONDS.sleep(10);
            countDoneSnils++;
            prWorkIndicator = roundDouble(((double) countDoneSnils/deleteListSize), 2, RoundingMode.HALF_UP);
            Platform.runLater(() -> prBarWork.setProgress(prWorkIndicator));

        }

        debugLog.add("--------------------------- Began process of records in files ----------------------------------");
        addTextAreaLine("Начинаю процесс создания отчетности в указанной папке: \"" + reportPath + "\"!");

        Platform.runLater(() -> prBarWriteData.setProgress(-1.0F));
        //---------------Запись общего файла -------------------
        WriteToFileSnilsList(reportPath, "FinalSnilsList_" + certStartDate + "_" + certEndDate + ".txt", header, indexList);

        //--Запись файла для Воронежа
        WriteToFileSnilsList(reportPath, "VRN_" + certStartDate + "_" + certEndDate + ".txt", header, indexListVRN);

        //--Запись файла для
        WriteToFileSnilsList(reportPath, "Mrm_" + certStartDate + "_" + certEndDate + ".txt", header, indexListMrm);

        //--Запись файла для ЯНАО
        WriteToFileSnilsList(reportPath, "YNAO_" + certStartDate + "_" + certEndDate + ".txt", header, indexListYNAO);

        //--Запись файла для Хабаровска
        WriteToFileSnilsList(reportPath, "Hab_" + certStartDate + "_" + certEndDate + ".txt", header, indexListHab);

        //--Запись файла для Сахалина
        WriteToFileSnilsList(reportPath, "Sah_" + certStartDate + "_" + certEndDate + ".txt", header, indexListSah);

        //--Запись файла для Пермский край
        WriteToFileSnilsList(reportPath, "Perm_" + certStartDate + "_" + certEndDate + ".txt", header, indexListPerm);

        //--Запись файла c некорректными строками
        WriteToFileSnilsString(reportPath, "IncorrectSnils_" + certStartDate + "_" + certEndDate + ".txt", header, incorrectListSnils);

        //--Запись файла cо строками второго списка, которые не получилось распарсить
        WriteToFileSnilsString(reportPath, "wrangListSnilsSecondList" + certStartDate + "_" + certEndDate + ".txt", header, wrangListSnilsSecondList);

                //--Запись файла c debug информацией
      //  WriteToFileSnilsString(reportPath, "DebugInfo" + certStartDate + "_" + certEndDate + ".txt", "", debugLog);

        Platform.runLater(() -> prBarWriteData.setProgress(1.0F));


        addTextAreaLine("Процесс создания отчетности в указанной папке: \"" + reportPath + "\" окончен!");

    }


    private double roundDouble(double roundedNumber, int roundingPrecision, RoundingMode roundingMethod){
        return new BigDecimal(roundedNumber).setScale(roundingPrecision, roundingMethod).doubleValue();
    }

    public String getDateFromStr(String strDate) {

        String pattern = "\\d{1,2}\\.\\d{1,2}\\.\\d{4}";

        Matcher matcher = Pattern.compile(pattern).matcher(strDate);
        if (!matcher.find()) {
            System.out.println("Date Not Found!");
           // addTextAreaLine("Дата не найдена!");
            return "Date Not Found!";
        }
        String dateString = strDate.substring(matcher.start(), matcher.end());
      //  System.out.println("In line date is found: " + dateString + ";");
        debugLog.add("В строке найдена дана: " + dateString + ";");
        return dateString;
    }

    public <T> LinkedList<T> DeleteDouble(List<T> listSnils) {
        return new LinkedList<>(new HashSet<>(listSnils));
    }


    public void WriteToFileSnilsList(String path, String nameFile, String header, LinkedList<Snils> snilsList) {
        if (snilsList.size() != 0) {
            String pathName = new StringBuilder()
                    .append(path)
                    .append(nameFile)
                    .toString();

            try (FileWriter writer = new FileWriter(pathName, false)) {
                // запись всей строки
               // System.out.println(pathName + " it is written down records - " + snilsList.size());

                debugLog.add(pathName + " it is written down records - " + snilsList.size());
                addTextAreaLine("В файл " + pathName + " записано записей - " + snilsList.size() + "!");

                writer.write(header + "\n");
                for (Snils snils : snilsList)
                    writer.write(snils.toString() + "\n");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());

            }
        }
    }

    public void WriteToFileSnilsString(String path, String nameFile, String header, LinkedList<String> snilsList) {

        if (snilsList.size() != 0) {
            String pathName = new StringBuilder()
                    .append(path)
                    .append(nameFile)
                    .toString();

            try (FileWriter writer = new FileWriter(pathName, false)) {
                // запись всей строки
                //System.out.println(pathName + " it is written down records - " + snilsList.size());

                debugLog.add(pathName + " it is written down records - " + snilsList.size());
                addTextAreaLine("В файл " + pathName + " записано записей - " + snilsList.size() + "!");

                writer.write(header + "\n");
                for (String snils : snilsList)
                    writer.write(snils.toString() + "\n");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                debugLog.add(ex.getMessage());
            }
        }
    }

    private void resetProgressIndiactor(){
       lblLoad1.setText("Загружено записей: ");
       lblLoad2.setText("Загружено записей: ");
       lblAllFile1.setText("Всего: ");
       lblAllFile2.setText("Всего: ");
    }
}

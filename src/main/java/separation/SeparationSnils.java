package separation;

import javafx.application.Platform;
import javafx.stage.Stage;
import separation.controllers.MainController;
import separation.objects.Snils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Created by VSKryukov on 15.01.2016.
*/
public class SeparationSnils {
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
public LinkedList<String> debugLog;
public String reportPath;
public String header;
public double prWorkIndicator = 0.0F;
public int deleteListSize;
public int expectedListFullSize;
        //
        LinkedList<Snils> indexList;
        LinkedList<Snils> indexListVRN;
        LinkedList<Snils> indexListMrm;
        LinkedList<Snils> indexListYNAO;
        LinkedList<Snils> indexListHab;
        LinkedList<Snils> indexListSah;
        LinkedList<Snils> indexListPerm;

    MainController mainController = new MainController();



public void SeparationSnilsListRun(Stage t, File fileOutloadListSnils, File fileListSnils, File dirOut) throws FileNotFoundException {


        Platform.runLater(() -> mainController.editTextArea.setText(""));
        debugLog = new LinkedList<>();
        // prBarReadData.setVisible(true);

        Long start_time = System.currentTimeMillis();

        debugLog.add("----------------------- Began of process of reading outload of snils List ----------------------");
        addTextAreaLine("Начинаю процесс чтения данных из файла выгрузки СНИЛСов!");


        Platform.runLater(() -> mainController.prBarReadData.setProgress(0.0F));


        readUnloadSnilsList(fileOutloadListSnils);



        //readUnloadSnilsList(fileOutloadListSnils);

        mainController.debugLog.add("--------------------------- Process of data reading is ended -----------------------------------");
        addTextAreaLine("Процесс чтения данных окончен!");

        debugLog.add("------------------------- Began of process of reading  of snils --------------------------------");
        addTextAreaLine("Начинаю процесс чтения данных из файла-спаска СНИЛСов!");

        //prBarReadData.setProgress(0.5F);
        Platform.runLater(() -> mainController.prBarReadData.setProgress(0.5F));
        readUnloadSnils(fileListSnils);

        Platform.runLater(() -> mainController.prBarReadData.setProgress(1.0F));

        debugLog.add("--------------------------- Process of data reading is ended -----------------------------------");
        addTextAreaLine("Процесс чтения данных окончен!");


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

//        System.out.println(mainController.editTextArea.getText());
        mainController.editTextArea.setText((mainController.editTextArea.getText() + str + "\n"));
        }




private void readUnloadSnilsList(File snilsOutload) throws FileNotFoundException {

        incorrectListSnils = new LinkedList<>();
        deleteList = new HashMap<>();

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
       // System.out.println(e.toString());

        incorrectListSnils.add(line);

        }
        }
        scanner.close();
       // Platform.runLater(() -> debugLog.add("From 1.txt it is loaded records - " + deleteList.size()));
       // System.out.println("In file 1.txt it is found records - " + countStr);
        mainController.debugLog.add("In file 1.txt it is found records - " + countStr);
       // System.out.println("From 1.txt it is loaded records - " + deleteList.size());
       // debugLog.add("From 1.txt it is loaded records - " + deleteList.size());
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

        //System.out.println("In file 2 it is found records - " + expectedListFull.size());
        //mainController.debugLog.add("In file 2 it is found records - " + expectedListFull.size());
       // expectedListFull = DeleteDouble(expectedListFull);
       // System.out.println("From 2.txt it is loaded records - " + expectedListFull.size());
       // debugLog.add("From 2.txt it is loaded records - " + expectedListFull.size());
       // expectedListFullSize = expectedListFull.size();
        }

private void separationListSnils () {
        indexList = new LinkedList<>();
        indexListVRN = new LinkedList<>();
        indexListMrm = new LinkedList<>();
        indexListYNAO = new LinkedList<>();
        indexListHab = new LinkedList<>();
        indexListSah = new LinkedList<>();
        indexListPerm = new LinkedList<>();


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



        // System.out.println("expectedList.size() befor - " + expectedListFull.size());




        boolean flag = true;
        mainController.prBarWork.setProgress(0.0F);
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
            mainController.prBarWork.setProgress(prWorkIndicator);

        }

        debugLog.add("--------------------------- Began process of records in files ----------------------------------");
        addTextAreaLine("Начинаю процесс создания отчетности в указанной папке: \"" + reportPath + "\"!");

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
        WriteToFileSnilsString(reportPath, "DebugInfo" + certStartDate + "_" + certEndDate + ".txt", "", debugLog);


        debugLog.add("------------------------- Process of record in files is ended ------------------------------");
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
        addTextAreaLine("Дата не найдена!");
        return "Date Not Found!";
        }
        String dateString = strDate.substring(matcher.start(), matcher.end());
        System.out.println("In line date is found: " + dateString + ";");
        addTextAreaLine("В строке найдена дана: " + dateString + ";");
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
        // prBarReadData.progressProperty()
//        prBarReadData.setVisible(false);
//        prBarWork.setVisible(false);
//        prBarWriteData.setVisible(false);
        }
//
}


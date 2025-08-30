import java.sql.*;
import java.util.Random;
import java.util.Scanner;

class Solarpanel {
    Scanner sc = new Scanner(System.in);
    Random r = new Random();
    double cosaveddaily;
    double cosavedmonthly;
    double cosavedyearly;
    double treequidaily;
    double treequimonthly;
    double treequiyearly;
    double dailyhousehold;
    double monthlyhousehold;
    double yearlyhousehold;
    double oldBill;
    int consumption;
    int panelarea;
    int panelage;
    int check;
    int paneltemp;
    int unitcost;
    Float instantirradiance;
    Float dailyirradiance;
    String Noon = "FullSun";
    String cloudy = "Partlycloudy";
    String cloudyday = "Cloudyday";
    String morning = "Morning";
    String evening = "Evening";


    Solarpanel(Connection con,Scanner sc) {
        System.out.println("option 1: Enter FullSun");
        System.out.println("option 2: Enter Partlycloudy");
        System.out.println("option 3: Enter Cloudyday");
        System.out.println("option 4: Enter Morning ");
        System.out.println("option 5: Enter Evening");
        String absorb = sc.nextLine();
        if (absorb.equalsIgnoreCase(Noon)) {
            float max = 1.0f;
            float min = 0.8f;
            instantirradiance = min + r.nextFloat() * (max - min);
            dailyirradiance = 5.0f + r.nextFloat() * (7.0f - 5.0f);
            float base_eff = 0.18f+r.nextFloat()*(0.22f-0.18f);
            try{
                String query =( "INSERT INTO info1(PanelArea,Daily_Irradiance,Instant_Irradiance,Adjusted_Efficiency," +
                        " NET_Efficiency,INS_POWER ,DAILY_ENENRGY,MONTHLY_ENERGY,YEARLY_ENERGY ) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)");
                String query2 = "INSERT INTO savings(DAILY_MONEYSAVED, MONTHLY_MONEYSAVED, YEARLY_MONEYSAVED, " +
                        "DAILYGRID_USAGE, MONTHLYGRID_USAGE, YEARLYGRID_USAGE, DAILY_NEWBILL, MONTHLY_NEWBILL, " +
                        "YEARLY_NEWBILL, CO_SAVED_DAILY, CO_SAVED_MONTHLY, CO_SAVED_YEARLY, TREE_EQUI_DAILY, " +
                        "TREE_EQUI_MONTHLY, TREE_EQUI_YEARLY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                try(PreparedStatement ps = con.prepareStatement(query)) {
                    try (PreparedStatement ps2 = con.prepareStatement(query2)) {
                        System.out.print("Please enter you PanelArea: ");
                        panelarea = sc.nextInt();
                        System.out.println();
                        System.out.print("Please enter you SolarPanel Age: ");
                        panelage = sc.nextInt();
                        System.out.println();
                        System.out.print("Enter panel temperature in celsius: ");
                        check = sc.nextInt();
                        if (check >= 30 && check <= 45) {
                            paneltemp = check;
                        } else {
                            paneltemp = 25;
                        }
                        System.out.println();
                        System.out.print("Please enter consumption unit: ");
                        consumption = sc.nextInt();
                        if (consumption >= 0 && consumption <= 200) {
                            unitcost = 3;
                        } else if (consumption >= 201 && consumption <= 400) {
                            unitcost = 5;
                        } else if (consumption >= 401 && consumption <= 800) {
                            unitcost = 6;
                        } else if (consumption >= 801 && consumption <= 1200) {
                            unitcost = 7;
                        } else {
                            unitcost = 8;
                        }
                        System.out.println();
                        System.out.print("Enter your OldBill: ");
                        oldBill = sc.nextInt();
                        monthlyhousehold = oldBill / unitcost;
                        dailyhousehold = monthlyhousehold / 30.5;
                        yearlyhousehold = monthlyhousehold * 12;
                        double eff = Adj_Eff(base_eff, panelage, paneltemp);
                        double lossfactor = Lossfactor();
                        double neteff = Net_Eff(eff, lossfactor);

                        double inspow = inst_Power(panelarea, instantirradiance, neteff);
                        double daily =  Daily_Energy(panelarea, dailyirradiance, neteff);
                        double monthly = Monthly_Energy(panelarea, dailyirradiance, neteff);
                        double yearly =  Yearly_Energy(panelarea, dailyirradiance, neteff);

                        double dailysavings = DailyMoneysavings(daily, unitcost);
                        double monthlysavings =  MonthlyMoneysavings(monthly, unitcost);
                        double yearlysavings =  YearlyMoneysavings(yearly, unitcost);

                        double dailygridusage =  dailyGridUsage(dailyhousehold, daily);
                        double monthlygridusage = MonthlyGridUsage(monthlyhousehold, monthly);
                        double yearlygridusage =  YearlyGridUsage(yearlyhousehold, yearly);

                        double dailynewbill = dailynewbill(dailygridusage, unitcost);
                        double monthlynewbill = monthlynewbill(monthlygridusage, unitcost);
                        double yearlynewbill = yearlynewbill(yearlygridusage, unitcost);

                        cosaveddaily = coSaved(daily);
                        cosavedmonthly =cosaveddaily*30;
                        cosavedyearly = cosaveddaily*365;

                        treequidaily =  treesEquivalent(daily, cosaveddaily);
                        treequimonthly = treesEquivalent(monthly, cosavedmonthly);
                        treequiyearly = treesEquivalent(yearly, cosavedyearly);
                        ps.setInt(1, panelarea);
                        ps.setFloat(2, dailyirradiance);
                        ps.setFloat(3, instantirradiance);
                        ps.setDouble(4, eff);
                        ps.setDouble(5, neteff);
                        ps.setDouble(6, inspow);
                        ps.setDouble(7, daily);
                        ps.setDouble(8, monthly);
                        ps.setDouble(9, yearly);
                        ps2.setDouble(1,dailysavings);
                        ps2.setDouble(2,monthlysavings);
                        ps2.setDouble(3,yearlysavings);
                        ps2.setDouble(4,dailygridusage);
                        ps2.setDouble(5,monthlygridusage);
                        ps2.setDouble(6,yearlygridusage);
                        ps2.setDouble(7,dailynewbill);
                        ps2.setDouble(8,monthlynewbill);
                        ps2.setDouble(9,yearlynewbill);
                        ps2.setDouble(10,cosaveddaily);
                        ps2.setDouble(11,cosavedmonthly);
                        ps2.setDouble(12,cosavedyearly);
                        ps2.setDouble(13,treequidaily);
                        ps2.setDouble(14,treequimonthly);
                        ps2.setDouble(15,treequiyearly);

                        int rowsaffect = ps.executeUpdate();
                        int rowsaff2 = ps2.executeUpdate();
                        if (rowsaffect > 0 && rowsaff2 > 0) {
                            System.out.println("data saved successfully");
                        } else {
                            System.out.println("Please retry");
                        }
                        System.out.printf("ID\tIrrad\tD_Irrad\tBaseEff\tAdjEff\tLoss\tNetEff\tArea\tPower\tD_Energy\tM_Energy\tY_Energy");
                        ResultSet rs1 = con.prepareStatement("SELECT * FROM info1").executeQuery();
                        while (rs1.next()) {
                            for (int i = 1; i <= 10; i++) {
                                System.out.print(rs1.getString(i) + "\t");
                            }
                            System.out.println();
                        }
                        PreparedStatement psS2 = con.prepareStatement("SELECT * FROM savings");
                        System.out.printf("ID\tD_Save\tM_Save\tY_Save\tD_Grid\tM_Grid\tY_Grid\tD_Bill\tM_Bill\tY_Bill\tCO_D\tCO_M\tCO_Y\tT_D\tT_M\tT_Y");
                        ResultSet rs2 = psS2.executeQuery();
                        while (rs2.next()) {
                            for (int i = 1; i <= 16; i++) {
                                System.out.print(rs2.getString(i) + "\t");
                            }
                            System.out.println();

                        }




                    }
                }

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
        if (absorb.equalsIgnoreCase(cloudy)) {
            float max = 0.7f;
            float min = 0.5f;
            instantirradiance = min + r.nextFloat() * (max - min);
            dailyirradiance = 2.5f + r.nextFloat() * (4.0f - 2.5f);
            float base_eff = 0.14f+r.nextFloat()*(0.17f-0.14f);
            try{
                String query =( "INSERT INTO info1(PanelArea,Daily_Irradiance,Instant_Irradiance,Adjusted_Efficiency," +
                        " NET_Efficiency,INS_POWER ,DAILY_ENENRGY,MONTHLY_ENERGY,YEARLY_ENERGY ) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)");
                String query2 = "INSERT INTO savings(DAILY_MONEYSAVED, MONTHLY_MONEYSAVED, YEARLY_MONEYSAVED, " +
                        "DAILYGRID_USAGE, MONTHLYGRID_USAGE, YEARLYGRID_USAGE, DAILY_NEWBILL, MONTHLY_NEWBILL, " +
                        "YEARLY_NEWBILL, CO_SAVED_DAILY, CO_SAVED_MONTHLY, CO_SAVED_YEARLY, TREE_EQUI_DAILY, " +
                        "TREE_EQUI_MONTHLY, TREE_EQUI_YEARLY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                try(PreparedStatement ps = con.prepareStatement(query)) {
                    try (PreparedStatement ps2 = con.prepareStatement(query2)) {


                        System.out.print("Please enter you PanelArea: ");
                        panelarea = sc.nextInt();
                        System.out.println();
                        System.out.print("Please enter you SolarPanel Age: ");
                        panelage = sc.nextInt();
                        System.out.println();
                        System.out.print("Enter panel temperature in celsius: ");
                        check = sc.nextInt();
                        if (check >= 30 && check <= 45) {
                            paneltemp = check;
                        } else {
                            paneltemp = 25;
                        }
                        System.out.println();
                        System.out.print("Please enter consumption unit: ");
                        consumption = sc.nextInt();
                        if (consumption >= 0 && consumption <= 200) {
                            unitcost = 3;
                        } else if (consumption >= 201 && consumption <= 400) {
                            unitcost = 5;
                        } else if (consumption >= 401 && consumption <= 800) {
                            unitcost = 6;
                        } else if (consumption >= 801 && consumption <= 1200) {
                            unitcost = 7;
                        } else {
                            unitcost = 8;
                        }
                        System.out.println();
                        System.out.print("Enter your OldBill: ");
                        oldBill = sc.nextInt();
                        monthlyhousehold = oldBill / unitcost;
                        dailyhousehold = monthlyhousehold / 30.5;
                        yearlyhousehold = monthlyhousehold * 12;
                        double eff = Adj_Eff(base_eff, panelage, paneltemp);
                        double lossfactor = Lossfactor();
                        double neteff = Net_Eff(eff, lossfactor);

                        double inspow = inst_Power(panelarea, instantirradiance, neteff);
                        double daily =  Daily_Energy(panelarea, dailyirradiance, neteff);
                        double monthly = Monthly_Energy(panelarea, dailyirradiance, neteff);
                        double yearly =  Yearly_Energy(panelarea, dailyirradiance, neteff);

                        double dailysavings = DailyMoneysavings(daily, unitcost);
                        double monthlysavings =  MonthlyMoneysavings(monthly, unitcost);
                        double yearlysavings =  YearlyMoneysavings(yearly, unitcost);

                        double dailygridusage =  dailyGridUsage(dailyhousehold, daily);
                        double monthlygridusage = MonthlyGridUsage(monthlyhousehold, monthly);
                        double yearlygridusage =  YearlyGridUsage(yearlyhousehold, yearly);

                        double dailynewbill = dailynewbill(dailygridusage, unitcost);
                        double monthlynewbill = monthlynewbill(monthlygridusage, unitcost);
                        double yearlynewbill = yearlynewbill(yearlygridusage, unitcost);

                        cosaveddaily = coSaved(daily);
                        cosavedmonthly =cosaveddaily*30;
                        cosavedyearly = cosaveddaily*365;

                        treequidaily =  treesEquivalent(daily, cosaveddaily);
                        treequimonthly = treesEquivalent(monthly, cosavedmonthly);
                        treequiyearly = treesEquivalent(yearly, cosavedyearly);

                        ps.setInt(1, panelarea);
                        ps.setFloat(2, dailyirradiance);
                        ps.setFloat(3, instantirradiance);
                        ps.setDouble(4, eff);
                        ps.setDouble(5, neteff);
                        ps.setDouble(6, inspow);
                        ps.setDouble(7, daily);
                        ps.setDouble(8, monthly);
                        ps.setDouble(9, yearly);
                        ps2.setDouble(1,dailysavings);
                        ps2.setDouble(2,monthlysavings);
                        ps2.setDouble(3,yearlysavings);
                        ps2.setDouble(4,dailygridusage);
                        ps2.setDouble(5,monthlygridusage);
                        ps2.setDouble(6,yearlygridusage);
                        ps2.setDouble(7,dailynewbill);
                        ps2.setDouble(8,monthlynewbill);
                        ps2.setDouble(9,yearlynewbill);
                        ps2.setDouble(10,cosaveddaily);
                        ps2.setDouble(11,cosavedmonthly);
                        ps2.setDouble(12,cosavedyearly);
                        ps2.setDouble(13,treequidaily);
                        ps2.setDouble(14,treequimonthly);
                        ps2.setDouble(15,treequiyearly);

                        int rowsaffect = ps.executeUpdate();
                        int rowsaff2 = ps2.executeUpdate();
                        if (rowsaffect > 0 && rowsaff2 > 0) {
                            System.out.println("data saved successfully");
                        } else {
                            System.out.println("Please retry");
                        }
                        System.out.printf("ID\tIrrad\tD_Irrad\tBaseEff\tAdjEff\tLoss\tNetEff\tArea\tPower\tD_Energy\tM_Energy\tY_Energy");
                        ResultSet rs1 = con.prepareStatement("SELECT * FROM info1").executeQuery();
                        while (rs1.next()) {
                            for (int i = 1; i <= 10; i++) {
                                System.out.print(rs1.getString(i) + "\t");
                            }
                            System.out.println();
                        }
                        PreparedStatement psS2 = con.prepareStatement("SELECT * FROM savings");
                        System.out.printf("ID\tD_Save\tM_Save\tY_Save\tD_Grid\tM_Grid\tY_Grid\tD_Bill\tM_Bill\tY_Bill\tCO_D\tCO_M\tCO_Y\tT_D\tT_M\tT_Y");
                        ResultSet rs2 = psS2.executeQuery();
                        while (rs2.next()) {
                            for (int i = 1; i <= 16; i++) {
                                System.out.print(rs2.getString(i) + "\t");
                            }
                            System.out.println();

                        }




                    }
                }

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }

        }
        if (absorb.equalsIgnoreCase(cloudyday)) {
            float max = 0.5f;
            float min = 0.1f;
            instantirradiance = min + r.nextFloat() * (max - min);
            dailyirradiance = 1.5f + r.nextFloat() * (2.5f - 1.5f);
            float base_eff = 0.08f+r.nextFloat()*(0.13f-0.08f);
            try{
                String query =( "INSERT INTO info1(PanelArea,Daily_Irradiance,Instant_Irradiance,Adjusted_Efficiency," +
                        " NET_Efficiency,INS_POWER ,DAILY_ENENRGY,MONTHLY_ENERGY,YEARLY_ENERGY ) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)");
                String query2 = "INSERT INTO savings(DAILY_MONEYSAVED, MONTHLY_MONEYSAVED, YEARLY_MONEYSAVED, " +
                        "DAILYGRID_USAGE, MONTHLYGRID_USAGE, YEARLYGRID_USAGE, DAILY_NEWBILL, MONTHLY_NEWBILL, " +
                        "YEARLY_NEWBILL, CO_SAVED_DAILY, CO_SAVED_MONTHLY, CO_SAVED_YEARLY, TREE_EQUI_DAILY, " +
                        "TREE_EQUI_MONTHLY, TREE_EQUI_YEARLY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                try(PreparedStatement ps = con.prepareStatement(query)) {
                    try (PreparedStatement ps2 = con.prepareStatement(query2)) {

                        System.out.print("Please enter you PanelArea: ");
                        panelarea = sc.nextInt();
                        System.out.println();
                        System.out.print("Please enter you SolarPanel Age: ");
                        panelage = sc.nextInt();
                        System.out.println();
                        System.out.print("Enter panel temperature in celsius: ");
                        check = sc.nextInt();
                        if (check >= 30 && check <= 45) {
                            paneltemp = check;
                        } else {
                            paneltemp = 25;
                        }
                        System.out.println();
                        System.out.print("Please enter consumption unit: ");
                        consumption = sc.nextInt();
                        if (consumption >= 0 && consumption <= 200) {
                            unitcost = 3;
                        } else if (consumption >= 201 && consumption <= 400) {
                            unitcost = 5;
                        } else if (consumption >= 401 && consumption <= 800) {
                            unitcost = 6;
                        } else if (consumption >= 801 && consumption <= 1200) {
                            unitcost = 7;
                        } else {
                            unitcost = 8;
                        }
                        System.out.println();
                        System.out.print("Enter your OldBill: ");
                        oldBill = sc.nextInt();
                        monthlyhousehold = oldBill / unitcost;
                        dailyhousehold = monthlyhousehold / 30.5;
                        yearlyhousehold = monthlyhousehold * 12;
                        double eff = Adj_Eff(base_eff, panelage, paneltemp);
                        double lossfactor = Lossfactor();
                        double neteff = Net_Eff(eff, lossfactor);

                        double inspow = inst_Power(panelarea, instantirradiance, neteff);
                        double daily =  Daily_Energy(panelarea, dailyirradiance, neteff);
                        double monthly = Monthly_Energy(panelarea, dailyirradiance, neteff);
                        double yearly =  Yearly_Energy(panelarea, dailyirradiance, neteff);

                        double dailysavings = DailyMoneysavings(daily, unitcost);
                        double monthlysavings =  MonthlyMoneysavings(monthly, unitcost);
                        double yearlysavings =  YearlyMoneysavings(yearly, unitcost);

                        double dailygridusage =  dailyGridUsage(dailyhousehold, daily);
                        double monthlygridusage = MonthlyGridUsage(monthlyhousehold, monthly);
                        double yearlygridusage =  YearlyGridUsage(yearlyhousehold, yearly);

                        double dailynewbill = dailynewbill(dailygridusage, unitcost);
                        double monthlynewbill = monthlynewbill(monthlygridusage, unitcost);
                        double yearlynewbill = yearlynewbill(yearlygridusage, unitcost);

                        cosaveddaily = coSaved(daily);
                        cosavedmonthly =cosaveddaily*30;
                        cosavedyearly = cosaveddaily*365;

                        treequidaily =  treesEquivalent(daily, cosaveddaily);
                        treequimonthly = treesEquivalent(monthly, cosavedmonthly);
                        treequiyearly = treesEquivalent(yearly, cosavedyearly);
                        ps.setInt(1, panelarea);
                        ps.setFloat(2, dailyirradiance);
                        ps.setFloat(3, instantirradiance);
                        ps.setDouble(4, eff);
                        ps.setDouble(5, neteff);
                        ps.setDouble(6, inspow);
                        ps.setDouble(7, daily);
                        ps.setDouble(8, monthly);
                        ps.setDouble(9, yearly);
                        ps2.setDouble(1, dailysavings);
                        ps2.setDouble(2, monthlysavings);
                        ps2.setDouble(3, yearlysavings);
                        ps2.setDouble(4, dailygridusage);
                        ps2.setDouble(5, monthlygridusage);
                        ps2.setDouble(6, yearlygridusage);
                        ps2.setDouble(7, dailynewbill);
                        ps2.setDouble(8, monthlynewbill);
                        ps2.setDouble(9, yearlynewbill);
                        ps2.setDouble(10, cosaveddaily);
                        ps2.setDouble(11, cosavedmonthly);
                        ps2.setDouble(12, cosavedyearly);
                        ps2.setDouble(13, treequidaily);
                        ps2.setDouble(14, treequimonthly);
                        ps2.setDouble(15, treequiyearly);

                        int rowsaffect = ps.executeUpdate();
                        int rowsaff2 = ps2.executeUpdate();
                        if (rowsaffect > 0 && rowsaff2 > 0) {
                            System.out.println("data saved successfully");
                        } else {
                            System.out.println("Please retry");
                        }
                        System.out.printf("ID\tIrrad\tD_Irrad\tBaseEff\tAdjEff\tLoss\tNetEff\tArea\tPower\tD_Energy\tM_Energy\tY_Energy");
                        ResultSet rs1 = con.prepareStatement("SELECT * FROM info1").executeQuery();
                        while (rs1.next()) {
                            for (int i = 1; i <= 10; i++) {
                                System.out.print(rs1.getString(i) + "\t");
                            }
                            System.out.println();
                        }
                        PreparedStatement psS2 = con.prepareStatement("SELECT * FROM savings");
                        System.out.printf("ID\tD_Save\tM_Save\tY_Save\tD_Grid\tM_Grid\tY_Grid\tD_Bill\tM_Bill\tY_Bill\tCO_D\tCO_M\tCO_Y\tT_D\tT_M\tT_Y");
                        ResultSet rs2 = psS2.executeQuery();
                        while (rs2.next()) {
                            for (int i = 1; i <= 16; i++) {
                                System.out.print(rs2.getString(i) + "\t");
                            }
                            System.out.println();


                        }
                    }
                }

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }

        } else if (absorb.equalsIgnoreCase(morning) || absorb.equalsIgnoreCase(evening)) {
            float max = 0.3f;
            float min = 0.1f;
            instantirradiance = min + r.nextFloat() * (max - min);
            dailyirradiance = 0.5f + r.nextFloat() * (1.0f - 0.5f);
            float base_eff = 0.05f+r.nextFloat()*(0.10f-0.05f);
            try{
                String query =( "INSERT INTO info1(PanelArea,Daily_Irradiance,Instant_Irradiance,Adjusted_Efficiency," +
                        " NET_Efficiency,INS_POWER ,DAILY_ENENRGY,MONTHLY_ENERGY,YEARLY_ENERGY ) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)");
                String query2 = "INSERT INTO savings(DAILY_MONEYSAVED, MONTHLY_MONEYSAVED, YEARLY_MONEYSAVED, " +
                        "DAILYGRID_USAGE, MONTHLYGRID_USAGE, YEARLYGRID_USAGE, DAILY_NEWBILL, MONTHLY_NEWBILL, " +
                        "YEARLY_NEWBILL, CO_SAVED_DAILY, CO_SAVED_MONTHLY, CO_SAVED_YEARLY, TREE_EQUI_DAILY, " +
                        "TREE_EQUI_MONTHLY, TREE_EQUI_YEARLY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                try(PreparedStatement ps = con.prepareStatement(query)) {
                    try (PreparedStatement ps2 = con.prepareStatement(query2)) {

                        System.out.print("Please enter you PanelArea: ");
                        panelarea = sc.nextInt();
                        System.out.println();
                        System.out.print("Please enter you SolarPanel Age: ");
                        panelage = sc.nextInt();
                        System.out.println();
                        System.out.print("Enter panel temperature in celsius: ");
                        check = sc.nextInt();
                        if (check >= 30 && check <= 45) {
                            paneltemp = check;
                        } else {
                            paneltemp = 25;
                        }
                        System.out.println();
                        System.out.print("Please enter consumption unit: ");
                        consumption = sc.nextInt();
                        if (consumption >= 0 && consumption <= 200) {
                            unitcost = 3;
                        } else if (consumption >= 201 && consumption <= 400) {
                            unitcost = 5;
                        } else if (consumption >= 401 && consumption <= 800) {
                            unitcost = 6;
                        } else if (consumption >= 801 && consumption <= 1200) {
                            unitcost = 7;
                        } else {
                            unitcost = 8;
                        }
                        System.out.println();
                        System.out.print("Enter your OldBill: ");
                        oldBill = sc.nextInt();
                        monthlyhousehold = oldBill / unitcost;
                        dailyhousehold = monthlyhousehold / 30.5;
                        yearlyhousehold = monthlyhousehold * 12;
                        double eff = Adj_Eff(base_eff, panelage, paneltemp);
                        double lossfactor = Lossfactor();
                        double neteff = Net_Eff(eff, lossfactor);

                        double inspow = inst_Power(panelarea, instantirradiance, neteff);
                        double daily =  Daily_Energy(panelarea, dailyirradiance, neteff);
                        double monthly = Monthly_Energy(panelarea, dailyirradiance, neteff);
                        double yearly =  Yearly_Energy(panelarea, dailyirradiance, neteff);

                        double dailysavings = DailyMoneysavings(daily, unitcost);
                        double monthlysavings =  MonthlyMoneysavings(monthly, unitcost);
                        double yearlysavings =  YearlyMoneysavings(yearly, unitcost);

                        double dailygridusage =  dailyGridUsage(dailyhousehold, daily);
                        double monthlygridusage = MonthlyGridUsage(monthlyhousehold, monthly);
                        double yearlygridusage =  YearlyGridUsage(yearlyhousehold, yearly);

                        double dailynewbill = dailynewbill(dailygridusage, unitcost);
                        double monthlynewbill = monthlynewbill(monthlygridusage, unitcost);
                        double yearlynewbill = yearlynewbill(yearlygridusage, unitcost);

                        cosaveddaily = coSaved(daily);
                        cosavedmonthly =cosaveddaily*30;
                        cosavedyearly = cosaveddaily*365;

                        treequidaily =  treesEquivalent(daily, cosaveddaily);
                        treequimonthly = treesEquivalent(monthly, cosavedmonthly);
                        treequiyearly = treesEquivalent(yearly, cosavedyearly);


                        ps.setInt(1, panelarea);
                        ps.setFloat(2, dailyirradiance);
                        ps.setFloat(3, instantirradiance);
                        ps.setDouble(4, eff);
                        ps.setDouble(5, neteff);
                        ps.setDouble(6, inspow);
                        ps.setDouble(7, daily);
                        ps.setDouble(8, monthly);
                        ps.setDouble(9, yearly);
                        ps2.setDouble(1, dailysavings);
                        ps2.setDouble(2, monthlysavings);
                        ps2.setDouble(3, yearlysavings);
                        ps2.setDouble(4, dailygridusage);
                        ps2.setDouble(5, monthlygridusage);
                        ps2.setDouble(6, yearlygridusage);
                        ps2.setDouble(7, dailynewbill);
                        ps2.setDouble(8, monthlynewbill);
                        ps2.setDouble(9, yearlynewbill);
                        ps2.setDouble(10, cosaveddaily);
                        ps2.setDouble(11, cosavedmonthly);
                        ps2.setDouble(12, cosavedyearly);
                        ps2.setDouble(13, treequidaily);
                        ps2.setDouble(14, treequimonthly);
                        ps2.setDouble(15, treequiyearly);

                        int rowsaffect = ps.executeUpdate();
                        int rowsaff2 = ps2.executeUpdate();
                        if (rowsaffect > 0 && rowsaff2 > 0) {
                            System.out.println("data saved successfully");
                        } else {
                            System.out.println("Please retry");
                        }
                        System.out.printf("ID\tIrrad\tD_Irrad\tBaseEff\tAdjEff\tLoss\tNetEff\tArea\tPower\tD_Energy\tM_Energy\tY_Energy");
                        ResultSet rs1 = con.prepareStatement("SELECT * FROM info1").executeQuery();
                        while (rs1.next()) {
                            for (int i = 1; i <= 10; i++) {
                                System.out.print(rs1.getString(i) + "\t");
                            }
                            System.out.println();
                        }
                        PreparedStatement psS2 = con.prepareStatement("SELECT * FROM savings");
                        System.out.printf("ID\tD_Save\tM_Save\tY_Save\tD_Grid\tM_Grid\tY_Grid\tD_Bill\tM_Bill\tY_Bill\tCO_D\tCO_M\tCO_Y\tT_D\tT_M\tT_Y");
                        ResultSet rs2 = psS2.executeQuery();
                        while (rs2.next()) {
                            for (int i = 1; i <= 16; i++) {
                                System.out.print(rs2.getString(i) + "\t");
                            }
                            System.out.println();
                        }


                        System.out.println();
                    }





                }

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        } else {
            instantirradiance = 0.2f + r.nextFloat() * (0.25f - 0.2f);
            dailyirradiance = 4 + r.nextFloat() * (5 - 4);
            float base_eff = 0.18f;
            try{
                String query =( "INSERT INTO info1(PanelArea,Daily_Irradiance,Instant_Irradiance,Adjusted_Efficiency," +
                        " NET_Efficiency,INS_POWER ,DAILY_ENENRGY,MONTHLY_ENERGY,YEARLY_ENERGY ) " +
                        "VALUES(?,?,?,?,?,?,?,?,?)");
                String query2 = "INSERT INTO savings(DAILY_MONEYSAVED, MONTHLY_MONEYSAVED, YEARLY_MONEYSAVED, " +
                        "DAILYGRID_USAGE, MONTHLYGRID_USAGE, YEARLYGRID_USAGE, DAILY_NEWBILL, MONTHLY_NEWBILL, " +
                        "YEARLY_NEWBILL, CO_SAVED_DAILY, CO_SAVED_MONTHLY, CO_SAVED_YEARLY, TREE_EQUI_DAILY, " +
                        "TREE_EQUI_MONTHLY, TREE_EQUI_YEARLY) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                try(PreparedStatement ps = con.prepareStatement(query)){
                    try (PreparedStatement ps2 = con.prepareStatement(query2)) {
                        System.out.print("Please enter you PanelArea: ");
                        panelarea = sc.nextInt();
                        System.out.println();
                        System.out.print("Please enter you SolarPanel Age: ");
                        panelage = sc.nextInt();
                        System.out.println();
                        System.out.print("Enter panel temperature in celsius: ");
                        check = sc.nextInt();
                        if (check >= 30 && check <= 45) {
                            paneltemp = check;
                        } else {
                            paneltemp = 25;
                        }
                        System.out.println();
                        System.out.print("Please enter consumption unit: ");
                        consumption = sc.nextInt();
                        if (consumption >= 0 && consumption <= 200) {
                            unitcost = 3;
                        } else if (consumption >= 201 && consumption <= 400) {
                            unitcost = 5;
                        } else if (consumption >= 401 && consumption <= 800) {
                            unitcost = 6;
                        }
                        else if (consumption >= 801 && consumption <= 1200) {
                            unitcost = 7;
                        } else {
                            unitcost = 8;
                        }
                        System.out.println();
                        System.out.print("Enter your OldBill: ");
                        oldBill = sc.nextInt();
                        monthlyhousehold = oldBill / unitcost;
                        dailyhousehold = monthlyhousehold / 30.5;
                        yearlyhousehold = monthlyhousehold * 12;
                        double eff = Adj_Eff(base_eff, panelage, paneltemp);
                        double lossfactor = Lossfactor();
                        double neteff = Net_Eff(eff, lossfactor);

                        double inspow = inst_Power(panelarea, instantirradiance, neteff);
                        double daily =  Daily_Energy(panelarea, dailyirradiance, neteff);
                        double monthly = Monthly_Energy(panelarea, dailyirradiance, neteff);
                        double yearly =  Yearly_Energy(panelarea, dailyirradiance, neteff);

                        double dailysavings = DailyMoneysavings(daily, unitcost);
                        double monthlysavings =  MonthlyMoneysavings(monthly, unitcost);
                        double yearlysavings =  YearlyMoneysavings(yearly, unitcost);

                        double dailygridusage =  dailyGridUsage(dailyhousehold, daily);
                        double monthlygridusage = MonthlyGridUsage(monthlyhousehold, monthly);
                        double yearlygridusage =  YearlyGridUsage(yearlyhousehold, yearly);

                        double dailynewbill = dailynewbill(dailygridusage, unitcost);
                        double monthlynewbill = monthlynewbill(monthlygridusage, unitcost);
                        double yearlynewbill = yearlynewbill(yearlygridusage, unitcost);

                        cosaveddaily = coSaved(daily);
                        cosavedmonthly =cosaveddaily*30;
                        cosavedyearly = cosaveddaily*365;

                        treequidaily =  treesEquivalent(daily, cosaveddaily);
                        treequimonthly = treesEquivalent(monthly, cosavedmonthly);
                        treequiyearly = treesEquivalent(yearly, cosavedyearly);


                        ps.setInt(1, panelarea);
                        ps.setFloat(2, dailyirradiance);
                        ps.setFloat(3, instantirradiance);
                        ps.setDouble(4, eff);
                        ps.setDouble(5, neteff);
                        ps.setDouble(6, inspow);
                        ps.setDouble(7, daily);
                        ps.setDouble(8, monthly);
                        ps.setDouble(9, yearly);
                        ps2.setDouble(1,dailysavings);
                        ps2.setDouble(2,monthlysavings);
                        ps2.setDouble(3,yearlysavings);
                        ps2.setDouble(4,dailygridusage);
                        ps2.setDouble(5,monthlygridusage);
                        ps2.setDouble(6,yearlygridusage);
                        ps2.setDouble(7,dailynewbill);
                        ps2.setDouble(8,monthlynewbill);
                        ps2.setDouble(9,yearlynewbill);
                        ps2.setDouble(10,cosaveddaily);
                        ps2.setDouble(11,cosavedmonthly);
                        ps2.setDouble(12,cosavedyearly);
                        ps2.setDouble(13,treequidaily);
                        ps2.setDouble(14,treequimonthly);
                        ps2.setDouble(15,treequiyearly);

                        int rowsaffect = ps.executeUpdate();
                        int rowsaff2 = ps2.executeUpdate();
                        if (rowsaffect > 0 && rowsaff2 > 0) {
                            System.out.println("data saved successfully");
                        } else {
                            System.out.println("Please retry");
                        }
                        System.out.printf("ID\tIrrad\tD_Irrad\tBaseEff\tAdjEff\tLoss\tNetEff\tArea\tPower\tD_Energy\tM_Energy\tY_Energy");
                        ResultSet rs1 = con.prepareStatement("SELECT * FROM info1").executeQuery();
                        while (rs1.next()) {
                            for (int i = 1; i <= 10; i++) {
                                System.out.print(rs1.getString(i) + "\t");
                            }
                            System.out.println();
                        }
                        PreparedStatement psS2 = con.prepareStatement("SELECT * FROM savings");
                        System.out.printf("ID\tD_Save\tM_Save\tY_Save\tD_Grid\tM_Grid\tY_Grid\tD_Bill\tM_Bill\tY_Bill\tCO_D\tCO_M\tCO_Y\tT_D\tT_M\tT_Y");
                        ResultSet rs2 = psS2.executeQuery();
                        while (rs2.next()) {
                            for (int i = 1; i <= 16; i++) {
                                System.out.print(rs2.getString(i) + "\t");
                            }
                            System.out.println();
                        }




                    }
                }

            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }
    }
    static double Adj_Eff(float base_Eff, int panelage, int paneltemp) {
        double cal = Math.pow((1 - 0.005f), panelage);
        double adjusted_efficiency = (base_Eff * cal) - (0.005 * (paneltemp - 25));
        return Math.max(0.0, adjusted_efficiency);
    }
    static double Lossfactor() {
        double lossfactor;
        float dirt = 0.05f;
        float inverter = 0.03f;
        float wiring = 0.02f;

        lossfactor = 1 - (dirt + inverter + wiring);

        return lossfactor;
    }

    static double Net_Eff(double Adjust_Eff, Double lossfactor) {
        double efficiency = Adjust_Eff * lossfactor;
        if(efficiency<0){
            return Math.max(0.0,efficiency);
        }
        return efficiency;
    }

    static double inst_Power(int Area, float irradiance, double Net_eff) {
        double power = Area * irradiance * Net_eff * 1000;
        if(power<0){
            return Math.max(0.0,power);
        }
        return power;
    }

    static double Daily_Energy(int Area, float irradiance, double Net_eff) {
        Random r = new Random();
        int  sunlighthr = r.nextInt(4) + 4;

        double dailyEnergy = Area * irradiance * Net_eff * sunlighthr;
        if(dailyEnergy<0){
            return Math.max(0.0,dailyEnergy);
        }
        return dailyEnergy;
    }

    static double Monthly_Energy(int Area, float irradiance, double Net_eff) {
        Random r = new Random();
        Scanner sc = new Scanner(System.in);
        int sunlighthr = r.nextInt(4) + 4;

        double dailyEnergy = Area * irradiance * Net_eff * sunlighthr;
        double monthly_energy = dailyEnergy * 30 ;
        if(monthly_energy<0){
            return Math.max(0.0,monthly_energy);
        }
        return monthly_energy;
    }
    static double Yearly_Energy(int Area, float irradiance, double Net_eff) {
        Random r = new Random();
        Scanner sc = new Scanner(System.in);
        int sunlighthr = r.nextInt(4) + 4;

        double dailyEnergy = Area * irradiance * Net_eff * sunlighthr;
        double yearly_enerngy = dailyEnergy*365;
        if(yearly_enerngy<0){
            return Math.max(0.0,yearly_enerngy);
        }
        return yearly_enerngy;
    }

    static double DailyMoneysavings(double energy,float unitcost){

        double dailysavings = energy * unitcost;
        if(dailysavings<0){
            return Math.max(0.0,dailysavings);
        }
        return dailysavings;
    }
    static double MonthlyMoneysavings(double energy,float unitcost){

        double monthlysavings = energy * unitcost;
        if(monthlysavings<0){
            return Math.max(0.0,monthlysavings);
        }
        return monthlysavings;
    }
    static double YearlyMoneysavings(double energy,float unitcost){

        double yearly_savings = energy*unitcost;
        if(yearly_savings<0){
            return Math.max(0.0,yearly_savings);
        }
        return yearly_savings;
    }
    static double dailyGridUsage(double householdusage, double solarproduced){
        double dailygrid = householdusage - solarproduced;
        if(dailygrid<0){
            return Math.max(0.0,dailygrid);
        }
        return dailygrid;
    }
    static double MonthlyGridUsage(double householdusage, double solarproduced){
        double monthlygrid = householdusage - solarproduced;
        if(monthlygrid<0){
            return Math.max(0.0,monthlygrid);
        }
        return monthlygrid;
    }
    static double YearlyGridUsage(double householdusage, double solarproduced){
        double yearlygrid = householdusage - solarproduced;
        if(yearlygrid<0){
            return Math.max(0.0,yearlygrid);
        }
        return yearlygrid;
    }
    static double dailynewbill(double netgridusage, int unitcost){
        double dailynewbill = netgridusage * unitcost;
        if(dailynewbill<0){
            return Math.max(0.0,dailynewbill);
        }
        return dailynewbill;
    }
    static double monthlynewbill(double netgridusage, int unitcost){
        double monthlynewbill = netgridusage * unitcost;
        if(monthlynewbill<0){
            return Math.max(0.0,monthlynewbill);
        }
        return monthlynewbill;
    }
    static double yearlynewbill(double netgridusage, int unitcost){
        double yearlynewbill = netgridusage * unitcost;
        if(yearlynewbill<0){
            return Math.max(0.0,yearlynewbill);
        }
        return yearlynewbill;
    }
    static double coSaved(double energy){
        double saved = energy *0.85;
        if(saved<0){
            System.out.println("not saving CO₂");
            return 0;
        }

        return saved;
    }
    static double treesEquivalent(double energy,double cosaved){
        double equivalent = cosaved/21;

        return equivalent;
    }


}


public class SolarIQ {
    private static final String url = "jdbc:mysql://localhost:3306/Solar";
    private static final String user = "root";
    private static final String password = "Password007";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            while(true){
                Scanner sc = new Scanner(System.in);
                Solarpanel solar = new Solarpanel(con,sc);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

}






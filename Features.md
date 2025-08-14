⚡ SolarIQ: Smart Solar Energy Efficiency & Savings Estimator SolarIQ is a Java-based console application that calculates and logs solar panel performance, efficiency, and savings data using realistic irradiance models and JDBC integration with MySQL.

🔧 Core Features 🌤️ Irradiance Simulation:

Models realistic sunlight conditions (e.g., FullSun, PartlyCloudy, CloudyDay, Morning, Evening)

Uses randomized values within expected ranges to simulate environmental variability

📈 Solar Panel Efficiency Calculation:

Calculates adjusted efficiency based on temperature and irradiance

Computes instant power, daily/monthly/yearly energy generation

💰 Cost Savings Estimator:

Calculates and stores daily, monthly, and yearly monetary savings

Computes new energy bill post solar adoption

🌱 Eco-Impact Estimation:

Calculates environmental impact in terms of trees saved

💾 Database Integration (MySQL via JDBC):

Stores all data into normalized MySQL tables like info1, savings

Supports parameterized queries for safe data insertion

🛠️ User Input System:

CLI-based interaction to gather solar panel attributes: area, age, temperature, usage, etc.

📂 Technologies Used Java (Core + JDBC)

MySQL

Randomized Simulation

Console I/O

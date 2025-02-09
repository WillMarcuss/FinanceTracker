# 📊 Finance Tracker

**Finance Tracker** is a modern financial management application that helps users track their expenses, manage budgets, and analyze spending habits with visual reports and statistical insights.

---

## 🚀 Features

- **User Authentication**: Secure login and registration with **BCrypt password hashing**.
- **Transaction Management**: Add, edit, and delete transactions with category filtering.
- **Budgeting**: Set and manage budgets for different expense categories.
- **Data Import & Export**:
    - Import transactions from **CSV**.
    - Export transaction reports and analysis as a **PDF**.
- **Data Visualization**:
    - **Pie Chart**: Displays spending distribution by category.
    - **Line Chart**: Shows spending trends over time.
    - **Bar Chart**: Compares category-wise spending.
    - **Heat Map**: Highlights spending frequency across days.
- **Advanced Analytics**:
    - Calculates **average daily spending**.
    - Displays **total expenses**.
    - Compares **budget vs. actual spending**.

---

## 🛠️ Tools & Technologies Used

### **💻 Programming Languages & Frameworks**
| Technology        | Description |
|------------------|-------------|
| **Java 17+**     | Core programming language |
| **JavaFX**       | GUI framework for creating a modern, interactive interface |
| **SQLite**       | Embedded relational database for data storage |
| **PDFBox**       | Java library for exporting reports as PDFs |

### **📊 Data Visualization**
| Technology        | Description |
|------------------|-------------|
| **JavaFX Charts** | Used for **Pie Charts**, **Line Charts**, **Bar Charts**, and **Heat Maps** to visualize data. |

### **🛡️ Security**
| Technology        | Description |
|------------------|-------------|
| **BCrypt**       | Secure password hashing to protect user credentials |

### **📁 File Handling**
| Technology        | Description |
|------------------|-------------|
| **Apache Commons CSV** | Handles CSV import/export functionality |

---

## 📌 Project Structure
```
📂 FinanceTracker
│── 📂 src
│   ├── 📂 controllers   # JavaFX controllers for UI interactions
│   ├── 📂 models        # Data models (User, Transaction, Budget)
│   ├── 📂 utils         # Helper classes for DB, security, and file handling
│   ├── 📂 views         # FXML UI layouts
│   ├── 📂 resources     # CSS, icons, and assets
│   ├── 📂 main          # Application entry point
│
│── finance.db           # SQLite database file
│
│── 📄 README.md         # Project documentation
│── 📄 .gitignore        # Git ignored files
```
---

## ⚙️ Setup & Installation

### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/WillMarcuss/FinanceTracker.git
cd FinanceTracker

2️⃣ Install Dependencies

Ensure you have Java 17+ and JavaFX installed.

If JavaFX is not configured, download the SDK and add it to your PATH.

3️⃣ Run the Application

javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml src/main/Main.java
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml main.Main

📂 Importing & Exporting Data

📥 Import Transactions from CSV
	•	Click the “Import CSV” button and select a .csv file.
	•	The system will parse and add transactions.

📤 Export Financial Report as PDF
	•	Click the “Export to PDF” button.
	•	A full report will be generated, including:
	•	Total expenses
	•	Spending trends
	•	Charts & graphs
	•	Budget analysis

📅 Future Improvements
	•	📲 Mobile App Integration (Using Kotlin or Flutter)
	•	🏦 Bank API Integration (Fetch transactions automatically)
	•	📊 More Data Analytics (Income tracking, savings visualization)
	•	☁️ Cloud Storage Support (Sync data across devices)

🤝 Contributing
	1.	Fork the repository
	2.	Create your feature branch (git checkout -b feature-name)
	3.	Commit your changes (git commit -m "Added feature")
	4.	Push to the branch (git push origin feature-name)
	5.	Create a Pull Request and wait for review

🛠️ Troubleshooting

Issue	Solution
App doesn’t start	Ensure JavaFX libraries are correctly linked
Database is locked	Close any other instances using SQLite
Export PDF error	Ensure Apache PDFBox is installed properly

📞 Contact

For questions or contributions, reach out:

📧 Email: williammarcus@cmail.carleton.ca
📂 GitHub: https://github.com/WillMarcuss
🌍 Website: https://williammarcus.netlify.app

Happy Budgeting! 🎯💰📊
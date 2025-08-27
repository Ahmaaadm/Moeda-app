# Modeo - Modern Accounting System

<div align="center">
  <h3>A sleek, modern desktop accounting application</h3>
  <p>Built with JavaFX and SQLite for small businesses and freelancers</p>
  
  [![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
  [![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
  [![SQLite](https://img.shields.io/badge/SQLite-3-green.svg)](https://www.sqlite.org/)
  [![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
</div>

---

## 📋 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Configuration](#-configuration)
- [Contributing](#-contributing)
- [Troubleshooting](#-troubleshooting)
- [License](#-license)
- [Contact](#-contact)

---

## 🌟 Overview

Modeo is a modern desktop accounting application built with **JavaFX** and **SQLite**, designed to simplify financial management for small businesses and freelancers. The application features a contemporary UI with Glassmorphism and Neumorphism design elements, providing an intuitive and visually appealing user experience.

With support for multiple currencies (LBP/USD), automated backups, and comprehensive reporting features, Modeo streamlines your accounting workflow while maintaining professional standards.

---

## ✨ Features

### 👥 **Employer Management**
- Add new employers with detailed information
- View and search through employer lists
- Delete and modify employer records
- Track employer-specific transactions

### 💰 **Cash Entry System**
- Dual currency support (**LBP** and **USD**)
- Real-time transaction recording
- Category-based expense tracking
- Income and expense differentiation

### 📊 **Reporting & Analytics**
- Daily transaction summaries
- Monthly financial reports
- Custom date range filtering
- Export capabilities for external analysis

### 💵 **Dollar Rate Management**
- Live exchange rate updates
- Historical rate tracking
- Manual rate adjustment options
- Automatic currency conversion

### ☁️ **Backup & Sync**
- Automatic database backups
- **Google Drive integration**
- Scheduled backup options
- One-click restore functionality

### 🎨 **Modern UI Design**
- Glassmorphism visual effects
- Neumorphism design elements
- Dark/Light theme support
- Responsive layout design

### 🌐 **Multi-language Support**
- Arabic language support
- English interface
- RTL (Right-to-Left) layout compatibility
- Localized number formatting

---



---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Core application development |
| **JavaFX** | 21 | Modern desktop UI framework |
| **SQLite** | 3.x | Lightweight embedded database |
| **CSS3** | - | Custom modern UI styling |
| **Inno Setup** | 6.x | Windows installer creation |
| **Launch4j** | 3.x | JAR to EXE conversion |
| **Google Drive API** | v3 | Cloud backup integration |

---

## 📋 Prerequisites

### For End Users (EXE Version)
- **Windows 10/11** (64-bit)
- No additional software required (bundled JRE included)

### For Developers (Source Code)
- **Java 21** or higher
- **JavaFX SDK 21** or higher
- **Maven** or **Gradle** (optional, for dependency management)
- **Git** for version control
- **IDE** (IntelliJ IDEA, Eclipse, VS Code recommended)

---

## 📦 Installation

### Option 1: Pre-built Executable (Recommended)

1. **Download** the latest release from [Releases](https://github.com/Ahmaaadm/Moeda-app/releases)
2. **Run** the `modeo-setup.exe` installer
3. **Follow** the installation wizard
4. **Launch** Modeo from the Start Menu or Desktop shortcut

### Option 2: From Source Code

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Ahmaaadm/Moeda-app.git
   cd Moeda-app
   ```

2. **Set up JavaFX (if not using an IDE):**
   ```bash
   # Download JavaFX SDK 21 from https://openjfx.io/
   export PATH_TO_FX=/path/to/javafx-sdk-21/lib
   ```

3. **Compile the application:**
   ```bash
   javac -d out --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml src/com/accounting/**/*.java
   ```

4. **Create JAR file:**
   ```bash
   jar -cfm modeo.jar manifest.txt -C out .
   ```

5. **Run the application:**
   ```bash
   java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar modeo.jar
   ```

### Option 3: Using Maven (if pom.xml is available)

```bash
git clone https://github.com/Ahmaaadm/Moeda-app.git
cd Moeda-app
mvn clean compile
mvn javafx:run
```

---

## 🚀 Usage

### First Launch
1. **Launch** Modeo from your desktop or Start Menu
2. **Configure** initial settings (currency preferences, backup location)
3. **Set up** your first employer profile
4. **Begin** recording transactions

### Daily Operations

#### Adding Employers
1. Navigate to **"Employers"** section
2. Click **"Add New Employer"**
3. Fill in employer details (name, contact, etc.)
4. **Save** the employer profile

#### Recording Transactions
1. Go to **"Cash Entries"** page
2. Select **employer** from dropdown
3. Enter **amount** and choose **currency** (LBP/USD)
4. Add **description** and select **category**
5. Click **"Save Entry"**

#### Viewing Reports
1. Access **"Reports"** section
2. Select **date range** using date pickers
3. Choose **report type** (Daily/Monthly)
4. **Filter** by employer or category
5. **Export** data if needed

#### Managing Dollar Rate
1. Open **"Settings"** > **"Dollar Rate"**
2. **Update** current exchange rate
3. **View** historical rate changes
4. **Enable** automatic updates (if available)

#### Backup & Restore
1. Go to **"Backup"** section
2. **Configure** Google Drive connection
3. **Schedule** automatic backups
4. Use **"Backup Now"** for immediate backup
5. **Restore** from previous backups when needed

---

## 📂 Project Structure

```
Modeo-app/
│
├── src/                          # Source code directory
│   └── com/
│       └── accounting/
│           ├── Main.java         # Application entry point
│           ├── controllers/      # JavaFX controllers
│           │   ├── MainController.java
│           │   ├── EmployerController.java
│           │   ├── CashEntryController.java
│           │   ├── ReportController.java
│           │   └── SettingsController.java
│           ├── models/           # Data models
│           │   ├── Employer.java
│           │   ├── CashEntry.java
│           │   ├── Report.java
│           │   └── Settings.java
│           ├── database/         # Database operations
│           │   ├── DatabaseHelper.java
│           │   ├── EmployerDAO.java
│           │   ├── CashEntryDAO.java
│           │   └── DatabaseInitializer.java
│           ├── utils/            # Utility classes
│           │   ├── CurrencyConverter.java
│           │   ├── DateUtils.java
│           │   ├── BackupManager.java
│           │   └── ExportUtils.java
│           └── services/         # Business logic
│               ├── GoogleDriveService.java
│               ├── ReportService.java
│               └── ValidationService.java
│
├── resources/                    # Application resources
│   ├── fxml/                    # FXML layout files
│   │   ├── main.fxml
│   │   ├── employer.fxml
│   │   ├── cash-entry.fxml
│   │   ├── reports.fxml
│   │   └── settings.fxml
│   ├── css/                     # Stylesheets
│   │   ├── main.css
│   │   ├── dark-theme.css
│   │   └── light-theme.css
│   ├── images/                  # Application images
│   │   ├── icons/
│   │   ├── backgrounds/
│   │   └── logos/
│   └── i18n/                    # Internationalization
│       ├── messages_en.properties
│       └── messages_ar.properties
│
├── database/                     # Database files
│   ├── modeo.db                 # Main SQLite database
│   └── backup/                  # Local backups
│
├── out/                         # Build output
│   ├── artifacts/               # JAR files
│   └── classes/                 # Compiled classes
│
├── installer/                   # Installation files
│   ├── modeo-setup.iss         # Inno Setup script
│   ├── launch4j-config.xml     # Launch4j configuration
│   └── resources/              # Installer resources
│
├── docs/                        # Documentation
│   ├── user-guide.md
│   ├── developer-guide.md
│   └── api-documentation/
│
├── screenshots/                 # Application screenshots
│   ├── dashboard.png
│   ├── cash-entry.png
│   └── reports.png
│
├── .gitignore                   # Git ignore rules
├── LICENSE                      # MIT License
├── README.md                    # This file
├── pom.xml                      # Maven configuration (if applicable)
└── manifest.txt                 # JAR manifest file
```

---

## ⚙️ Configuration

### Application Settings
The application settings are stored in `config/settings.properties`:

```properties
# Database Configuration
database.path=database/modeo.db
database.backup.interval=24
database.backup.path=backup/

# UI Configuration
ui.theme=dark
ui.language=en
ui.window.width=1200
ui.window.height=800

# Currency Settings
currency.default=USD
currency.exchange.auto_update=true
currency.exchange.api_key=your_api_key_here

# Backup Configuration
backup.google_drive.enabled=true
backup.google_drive.folder_id=your_folder_id
backup.local.enabled=true
backup.local.path=backup/
```

### Google Drive Integration Setup

1. **Create** a Google Cloud Project
2. **Enable** Google Drive API
3. **Generate** OAuth 2.0 credentials
4. **Download** `credentials.json` and place in `config/`
5. **Run** the application and authorize access

### Database Schema

The SQLite database includes these main tables:

```sql
-- Employers table
CREATE TABLE employers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    address TEXT,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Cash entries table
CREATE TABLE cash_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employer_id INTEGER,
    amount_lbp DECIMAL(15,2),
    amount_usd DECIMAL(15,2),
    description TEXT,
    entry_date DATE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES employers(id)
);

-- Exchange rates table
CREATE TABLE exchange_rates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    rate DECIMAL(10,4),
    effective_date DATE,
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Development Setup

1. **Fork** the repository
2. **Clone** your fork:
   ```bash
   git clone https://github.com/your-username/Moeda-app.git
   ```
3. **Create** a feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
4. **Make** your changes
5. **Test** thoroughly
6. **Commit** your changes:
   ```bash
   git commit -m "Add amazing feature"
   ```
7. **Push** to your branch:
   ```bash
   git push origin feature/amazing-feature
   ```
8. **Open** a Pull Request

### Code Style Guidelines

- **Follow** Java naming conventions
- **Use** meaningful variable and method names
- **Add** JavaDoc comments for public methods
- **Maintain** consistent indentation (4 spaces)
- **Write** unit tests for new features
- **Follow** SOLID principles

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn integration-test

# Generate test coverage report
mvn jacoco:report
```

---

## 🔧 Troubleshooting

### Common Issues

#### "JavaFX runtime components are missing"
**Solution:** Ensure JavaFX is properly installed and configured in your PATH.
```bash
# For Windows
set PATH_TO_FX=C:\path\to\javafx-sdk-21\lib
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar modeo.jar
```

#### Database connection errors
**Solution:** Check that the database file has proper read/write permissions:
```bash
chmod 644 database/modeo.db
```

#### Google Drive backup fails
**Solution:** 
1. Verify internet connection
2. Check `credentials.json` file exists
3. Re-authorize Google Drive access
4. Check API quotas in Google Cloud Console

#### High DPI display issues
**Solution:** Add JVM arguments:
```bash
-Dglass.gtk.uiScale=150%
-Dglass.win.uiScale=150%
```

### Performance Optimization

- **Regular database maintenance:** Use the built-in database cleanup tools
- **Limit report date ranges:** Large date ranges may slow down report generation
- **Close unused windows:** Keep only necessary windows open
- **Update regularly:** Newer versions include performance improvements

### Getting Help

- **Check** the [Issues](https://github.com/Ahmaaadm/Moeda-app/issues) page
- **Search** existing issues before creating a new one
- **Provide** detailed information when reporting bugs:
  - Operating system and version
  - Java version
  - Steps to reproduce
  - Error messages
  - Screenshots (if applicable)

---

## 🏗️ Building from Source

### Prerequisites for Building

- Java 21+ JDK
- JavaFX SDK 21+
- Maven 3.6+ (optional)
- Inno Setup 6+ (for Windows installer)

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package JAR
mvn package

# Create executable with Launch4j
# (Configure launch4j-config.xml first)
launch4j launch4j-config.xml

# Create Windows installer
# (Run in Inno Setup)
iscc installer/modeo-setup.iss
```

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Ahmad M.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📞 Contact

**Ahmad M.** - Project Maintainer

- **GitHub:** [@Ahmaaadm](https://github.com/Ahmaaadm)
- **Email:** [ahmadmoussa680@gmail.com](mailto:your-email@example.com)
- **LinkedIn:** [https://www.linkedin.com/in/ahmad-moussa-04bb0a263/](https://linkedin.com/in/your-profile)

**Project Link:** [https://github.com/Ahmaaadm/Moeda-app](https://github.com/Ahmaaadm/Moeda-app)

---

## 🙏 Acknowledgments

- **JavaFX Community** for the excellent UI framework
- **SQLite Team** for the reliable embedded database
- **Google Drive API** for seamless cloud integration
- **Inno Setup** for the Windows installer solution
- **All contributors** who helped improve this project

---

## 🚀 Roadmap

### Upcoming Features
- [ ] **Mobile companion app** (Android/iOS)
- [ ] **Advanced reporting** with charts and graphs
- [ ] **Multi-company support**
- [ ] **Invoice generation** and management
- [ ] **Tax calculation** and reporting
- [ ] **API integration** with accounting services
- [ ] **Plugin system** for extensibility
- [ ] **Advanced backup options** (Dropbox, OneDrive)

### Version History
- **v1.0.0** - Initial release with core features
- **v1.1.0** - Added Google Drive backup
- **v1.2.0** - Multi-language support
- **v2.0.0** - UI redesign with modern themes
- **v2.1.0** - Enhanced reporting features

---

<div align="center">
  <p>Made with ❤️ by <a href="https://github.com/Ahmaaadm">Ahmad M.</a></p>
  <p>⭐ Star this repo if you find it helpful!</p>
</div>

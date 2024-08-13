This document outlines the steps to properly install Python 3.8 and Pycharm on a Windows machine.

Prerequisites:

* Downloaded Python 3.8 and Pycharm Professional 2023.1 installation files. * 


Installation Steps:

Install Python 3.8 by following these steps:
a. Run the python-3.8.10-amd64.exe file.
b. Tick the check box "Add to PATH" during the installation process.
c. After installation, locate the Python 3.8 installation folder. Typically, it is found in C drive -> Users -> <Current User> -> AppData -> Local -> Programs -> Python -> Python 38.
d. Copy the file path of the Python 3.8 installation folder.

Edit the environment variables:
a. Open "Edit environment variables".
b. Click on "Edit" under System Variables.
c. Click on "Path", then click "New".
d. Paste the Python 3.8 installation folder path that was copied earlier.
e. Click "OK".

Verify that Python 3.8 was successfully installed:
a. Open CMD.
b. Type the command "python --version".
c. The Python version displayed should be 3.8.

Install Pycharm:
a. Run the pycharm-professional-2023.1.exe file.
b. Follow the prompts to complete the installation process.

Set up the Python Interpreter in Pycharm:
a. Open Pycharm.
b. Navigate to File -> Settings -> Project:<Project name> -> Python Interpreter.
c. Click on "Python Interpreter".
d. Click on "Add Interpreter" -> "Add Local Interpreter".
e. Click on "Existing" under Environment.
f. Locate the Python 3.8 installation folder path and select the python.exe file.

Install the omniorb-py package in Pycharm:
a. Click on the "+" icon in Pycharm.
b. Search for "omniorb-py" package and select it.
c. Follow the prompts to install the package.
d. A message should appear if the installation was successful.
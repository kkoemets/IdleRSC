import getpass
import os
import subprocess
from time import sleep

try:
    import psutil
except ImportError:
    import sys

    subprocess.check_call([sys.executable, "-m", "pip", "install", "psutil"])
    import psutil


class Account:
    def __init__(self, username: str, password: str):
        self.username = username
        self.password = password


class AccountProcess:
    def __init__(self, username: str, process: subprocess.Popen):
        self.username = username
        self.process = process


class AccountService:

    # constructor
    def __init__(self):
        self.account_file = "accounts.txt"

    def account_exists(self, username: str):
        with open(self.account_file, "r") as f:
            for line in f:
                if line.startswith(username + ":"):
                    return True
        return False

    def add_account(self, username: str, password: str) -> None:
        with open(self.account_file, "a") as f:
            f.write(username.strip() + ":" + password.strip() + "\n")

    def get_all_accounts(self) -> list[Account]:
        with open(self.account_file, "r") as f:
            return [Account(user_and_pw[0], user_and_pw[1]) for user_and_pw in [line.split(":") for line in f]]


class ProcessService:
    def __init__(self, accounts: AccountService):
        self.account_service = accounts
        self.account_processes = []

    def poll(self) -> None:
        self.account_processes = [process for process in self.account_processes if process.process.poll() is None]

    def get_accounts_without_process(self) -> list[Account]:
        return [account for account in self.account_service.get_all_accounts() if
                account.username not in [process.username for process in
                                         self.account_processes]]

    def start_process(self, account: Account) -> None:
        username = account.username.strip()
        print("Starting account {}...".format(username))
        password = account.password.strip()

        command = 'java -cp "IdleRSC.jar;patched_client.jar" bot.Main ' \
                  '--enablegfx "true" --debug "true" ' \
                  '--username "{}" --password "{}"'.format(username, password)

        process = subprocess.Popen(command, stdout=subprocess.DEVNULL, stderr=subprocess.STDOUT)
        process.username = username

        self.account_processes = self.account_processes + [AccountProcess(username, process)]

    def probe(self):
        for account_process in self.account_processes:
            print("Probing process for account '{}'...".format(account_process.username))
            try:
                stdout, stderr = account_process.process.communicate(timeout=10)
            except subprocess.TimeoutExpired:
                print("The process timed out.")
            else:
                if stdout:
                    print("The process wrote to standard output:")
                    print(stdout.decode())
                if stderr:
                    print("The process wrote to standard error:")
                    print(stderr.decode())


account_service = AccountService()
process_service = ProcessService(account_service)


def add_account_with_prompt() -> None:
    username = input("Enter a new username: ")
    if account_service.account_exists(username):
        print("Username already exists. Please try another.")
        return

    password = getpass.getpass("Enter a password: ")
    account_service.add_account(username, password)
    print("Account created successfully!")


def close_a_process_with_prompt(account_processes: list[AccountProcess]):
    selected_process = None
    while selected_process is None:
        process_options = []
        for i, account_process in enumerate(account_processes):
            process_options.append(str(i + 1) + ". " + account_process.username)
        selected_option = input("Select position number: \n" + "\n".join(process_options) + "\n")

        for i, account_process in enumerate(account_processes):
            if str(i + 1) == selected_option:
                selected_process = account_process
                break
        if selected_process is None:
            print("Invalid option. Exiting prompt. Please try again.")
            break
        else:
            for account_process in account_processes:
                if account_process.username == selected_process.username:
                    print("Killing process for account '{}'...".format(selected_process.username))
                    account_process.process.kill()
                    sleep(2)


def start_an_inactive_account_with_prompt(inactive_accounts: list[Account]) -> None:
    process_options = []
    for i, account in enumerate(inactive_accounts):
        process_options.append(str(i + 1) + ". " + account.username)
    selected_option = input("Select position number: \n" + "\n".join(process_options) + "\n")
    selected_account = None
    for i, account in enumerate(inactive_accounts):
        if str(i + 1) == selected_option:
            selected_account = account
            break
    if selected_account is None:
        print("Invalid option. Exiting prompt. Please try again.")
        return

    return process_service.start_process(selected_account)


def initialize_processes_with_prompt() -> None:
    if input("Start processes for all accounts? (y/n): ") == "y":
        for account in account_service.get_all_accounts():
            process_service.start_process(account)
    else:
        if input("Start a process for a specific account? (y/n): ") == "y":
            start_an_inactive_account_with_prompt(process_service.get_accounts_without_process())


def main() -> None:
    print("Welcome to EasyStart! by @kkoemets")
    print("")
    print("This program will help you start multiple accounts at once.")
    print("You can also start all accounts at any time from menus.")
    print("You can also start a specific account at any time from menus.")
    print("You can also close a process of an account at any time from menus.")
    print("You can also close all processes at any time by stopping the program.")
    print("")
    print("###IMPORTANT### Before you start using the tool, make sure you can run IdleRSC normally.")
    print("")

    account_file = account_service.account_file
    if not os.path.exists(account_file):
        print("Creating users file +" + account_file)
        open(account_file, "w").close()

    while True:
        if input("Do you want to create a new account? (y/n): ") == "y":
            add_account_with_prompt()
        else:
            break

    initialize_processes_with_prompt()
    while len(process_service.account_processes) == 0:
        print("Well, you have to decide something first..")
        initialize_processes_with_prompt()

    while len(process_service.account_processes) > 0:
        print("Processes running: {}".format(len(process_service.account_processes)))

        if input("Do you want to probe account processes? (y/n): ") == "y":
            process_service.probe()

        inactive_accounts = process_service.get_accounts_without_process()
        if len(inactive_accounts) > 0 and input("Start a process for an account? (y/n): ") == "y":
            start_an_inactive_account_with_prompt(inactive_accounts)

        if input("Close a process of an account? (y/n): ") == "y":
            close_a_process_with_prompt(process_service.account_processes)

        process_service.poll()

    print("All processes closed. No more accounts running. Exiting...")


if __name__ == "__main__":
    main()

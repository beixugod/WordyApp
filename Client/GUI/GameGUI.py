import random
import tkinter as tk
from tkinter import messagebox

import threading

from . import RankGUI, ResultGUI, Homepage


class GameGUI:
    def __init__(self, wordy_game, username, room_name):
        self.wordy_game = wordy_game
        self.username = username
        self.room_name = room_name
        first_letter_line = ""
        second_letter_line = ""

        self.room_info = wordy_game.getGameRoomInformation(room_name, username)
        points = self.room_info[1]

        # Set up the GUI components
        self.root = tk.Tk()
        self.root.title("Wordy")

        # Set the size and location of the GUI
        self.root.geometry("800x650")
        self.root.resizable(False, False)
        self.root.wm_attributes("-topmost", 1)
        self.root.configure(bg='#1c2833')

        # to set the size of the window
        self.window_width = 800
        self.window_height = 650
        self.round_label = tk.Label(self.root, text="Round " + self.room_info[3], font=('Arial', 40), fg='#DEA54B', bg='#1c2833')
        self.round_label.pack(pady=20, padx=0)

        self.timer = tk.Label(self.root, text="Timer", font=('Arial', 30), fg='#DEA54B', bg='#1c2833')
        self.timer.pack(pady=30, padx=0)

        self.countdown_label = tk.Label(self.root, text=str(10), font=('Arial', 25), fg='#DBCBD8', bg='#1c2833')
        self.countdown_label.place(x=385, y=185)

        self.user_label = tk.Label(self.root, text=username, font=('Arial', 17), fg='#E8DAB2', bg='#1c2833')
        self.user_label.place(x=10, y=10)

        self.score_label = tk.Label(self.root, text="Score: " + str(points), font=('Arial', 17),  fg='#DEA54B', bg='#1c2833')
        self.score_label.place(x=self.window_width - 105, y=10)

        self.word_label = tk.Label(self.root, text=first_letter_line + "\n" + second_letter_line,
                                   font=('Orbitron', 40, 'bold'), justify='center', fg='#CE796B', bg='#1c2833')
        self.word_label.pack(pady=50, padx=0)

        random_letters = self.room_info[4]
        self.random_letters = random_letters
        self.shuffle_letters(random_letters)

        self.validation = tk.Label(self.root, text="", font=('Arial', 17), bg='#1c2833')
        self.validation.place(relx=0.5, rely=0.85, anchor="center")

        # Create answer entry and bind it to events
        self.answer_entry = tk.Entry(self.root, font=('Arial', 30), justify='center',fg='#1c2833', bg='#DEA54B')
        self.answer_entry.pack(pady=30, padx=0)
        self.answer_entry.bind('<Return>', self.validate_answer)
        self.answer_entry.bind("<KeyRelease>", lambda e: self.on_answer_changed(e))

        self.shuffle_button = tk.Button(self.root, text="SHUFFLE", font=('Arial', 11), command=self.click_shuffle, fg='#FFFFFC', bg='#8B9556')
        self.shuffle_button.place(x=548, y=440)

        # Start the countdown by calling the method after a delay
        self.root.after(1000, self.run_countdown)

        self.root.protocol("WM_DELETE_WINDOW", self.close_window)

        # Run the Tkinter event loop
        self.root.mainloop()

    def on_answer_changed(self, event):
        # Convert the entered text to uppercase and remove any digits
        text = self.answer_entry.get().upper()
        text = ''.join([i for i in text if not i.isdigit()])
        self.answer_entry.delete(0, tk.END)
        self.answer_entry.insert(0, text)

    previous_time_value = ""
    repetition_count = 0

    def run_countdown(self):
        room_info = self.wordy_game.getGameRoomInformation(self.room_name, self.username)
        countdown_value = int(room_info[0])

        # start of timer end error
        if room_info[0] == self.previous_time_value:
            self.repetition_count += 1
            if self.repetition_count == 7:
                self.root.destroy()
                messagebox.showerror("Error", "The game unexpectedly ended! Returning to main menu.")
                Homepage.Homepage(self.wordy_game, self.username)
        else:
            self.repetition_count = 1
        self.previous_time_value = room_info[0]
        # end of timer end error

        if countdown_value > 0:
            self.countdown_label.config(text=str(countdown_value))
            countdown_value -= 1
            self.root.after(1000, self.run_countdown)
        else:
            if not self.wordy_game.endRound(self.username, self.room_name):
                self.root.withdraw()
                RankGUI.RankGUI(self.wordy_game, self.username, self.room_name)
                self.root.destroy()
            else:
                self.root.withdraw()
                ResultGUI.ResultGUI(self.wordy_game, self.room_name, self.username)
                self.root.destroy()

    def click_shuffle(self):
        self.shuffle_letters(self.random_letters)

    def shuffle_letters(self, random_letters):
        letters_array = list(random_letters)

        # THE FISHER-YALES SHUFFLE ALGORITHM
        for i in range(len(letters_array) - 1, 0, -1):
            index = random.randint(0, i)
            temp = letters_array[index]
            letters_array[index] = letters_array[i]
            letters_array[i] = temp

        first_letter_line = ''.join(letters_array[:10]).strip()
        second_letter_line = ''.join(letters_array[10:]).strip()

        first_letter_line = ' '.join(list(first_letter_line))
        second_letter_line = ' '.join(list(second_letter_line))

        self.word_label.config(text=first_letter_line + "\n" + second_letter_line)

    def validate_answer(self, event=None):
        answer = self.answer_entry.get().strip()
        if answer:
            if self.wordy_game.submitWord(answer, self.username, int(self.room_info[3]), self.room_name):
                self.validation.config(text="ANSWER IS VALID", fg="green")

            else:
                self.validation.config(text="INVALID WORD FORMED", fg="red")
        else:
            self.validation.config(text="", fg="black")

        self.answer_entry.delete(0, tk.END)
        self.root.update()

    def close_window(self):
        self.wordy_game.logout(self.username)
        self.wordy_game.leaveGameRoom(self.username, self.room_name)
        self.root.destroy()

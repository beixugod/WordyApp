import tkinter as tk
from tkinter import *
from . import Leaderboard
class TopFiveLongestWords:

    def __init__(self, wordy_game, username):
        # Set up the GUI components
        self.root = tk.Tk()
        self.root.title("Wordy Login")
        self.wordy_game = wordy_game
        self.username = username
        self.root.resizable(False, False)
        self.root.configure(bg='#1c2833')

        # to set the size of the window
        self.window_width = 500
        self.window_height = 430
        self.screen_width = self.root.winfo_screenwidth()
        self.screen_height = self.root.winfo_screenheight()
        self.x_position = int((self.screen_width - self.window_width) / 2)
        self.y_position = int((self.screen_height - self.window_height) / 2)
        self.root.geometry(
            "{}x{}+{}+{}".format(self.window_width, self.window_height, self.x_position, self.y_position))

        self.wordy_label = tk.Label(self.root, text="Top 5 Longest Words", font=('Arial', 25), fg='#FCA311', bg='#1c2833')
        self.wordy_label.pack(pady=20, padx=20)

        self.longest_words_pane = tk.Frame(self.root)

        self.longest_word_array = self.wordy_game.getFiveLongestWords()

        if len(self.longest_word_array) == 0:
            # No words recorded, displays that no records are read from the database
            self.no_words_label = tk.Label(self.longest_words_pane, text="No words recorded", font=('Arial', 20))
            self.no_words_label.pack(pady=50)
        else:
            self.longest_words_list = tk.Listbox(self.longest_words_pane, font=('Arial', 18), fg='#FCA311', bg='#1c2833',
                                                 height=5, width=30, )
            for word in self.longest_word_array:
                self.longest_words_list.insert(END, word)
            self.longest_words_list.pack(pady=10)

        self.longest_words_pane.place(relx=0.5, rely=0.5, anchor=CENTER)
        self.longest_words_pane.pack(pady=20)

        self.back_button = tk.Button(self.root, text="Back", font=('Arial', 20), command=self.back, fg='#0A090C', bg='#DEA54B')
        self.back_button.pack(pady=10)

        self.root.protocol("WM_DELETE_WINDOW", self.close_window)
        self.root.mainloop()

    def back(self):
        self.root.withdraw()
        Leaderboard.Leaderboard(self.wordy_game, self.username)
        self.root.destroy()

    def close_window(self):
        self.root.withdraw()
        self.wordy_game.logout(self.username)
        self.root.destroy()

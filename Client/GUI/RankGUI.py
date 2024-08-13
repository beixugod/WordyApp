import tkinter as tk
import threading
import time
from . import GameGUI

class RankGUI:
    def __init__(self, wordy_game, username, room_name):
        self.wordy_game = wordy_game
        self.username = username
        self.room_name = room_name
        # Set up the GUI components
        self.root = tk.Tk()
        self.root.title("Wordy")

        # Set the size and location of the GUI
        self.root.geometry("800x650")
        self.root.resizable(False, False)
        self.root.configure(bg='#1c2833')

        # to set the size of the window
        self.window_width = 800
        self.window_height = 650

        self.title_label = tk.Label(self.root, text=" SCORE BOARD ", font=('Arial', 40, 'bold'), fg='Lavender', bg='#1c2833')
        self.title_label.pack(pady=20, padx=0)

        self.app_label = tk.Label(self.root, text="W O R D Y", font=('Arial', 20), fg='tan', bg='#1c2833')
        self.app_label.place(x=328, y=90)

        self.next_label = tk.Label(self.root, text="Next round starts in ", font=('Arial', 12), fg='white', bg='#1c2833')
        self.next_label.place(x=320, y=595)

        self.countdown_label = tk.Label(self.root, text=str(7), font=('Arial', 14, 'bold'), fg='white', bg='#1c2833')
        self.countdown_label.place(x=458, y=593)

        # create a panel in the center of the GUI
        panel_width = 400
        panel_height = 440
        panel_x = (self.window_width - panel_width) / 2
        panel_y = (self.window_height - panel_height) / 2

        panel = tk.Frame(self.root, width=panel_width, height=panel_height, bd=1, relief=tk.RIDGE, bg='Beige')
        panel.place(x=200, y=140)

        # Create labels for the player names and scores
        player_labels = []
        score_labels = []
        roomInfo = self.wordy_game.getGameRoomInformation(room_name, username)
        players = roomInfo[5:]
        for i in range(len(players)):
            player = players[i]
            player_label = tk.Label(panel, text=player, font=('Arial', 14), fg='black', bg='Beige')
            if player == self.username:
                player_label.config(fg='#CE796B')  # Set the color to yellow for the logged-in player
            player_label.place(x=20, y=20 + 40 * i)

            # Get the player's score using their username
            temp_array = self.wordy_game.getGameRoomInformation(room_name, player)
            player_score = temp_array[1]
            score_label = tk.Label(panel, text=player_score, font=('Arial', 14), fg='black', bg='Beige')
            if player == self.username:
                score_label.config(fg='#CE796B')  # Set the color to yellow for the logged-in player's score
            score_label.place(x=320, y=20 + 40 * i)

            score_labels.append(score_label)
            player_labels.append(player_label)

        # Start the countdown by calling the method after a delay
        self.root.after(1000, self.run_countdown)

        self.root.protocol("WM_DELETE_WINDOW", self.close_window)

        # Run the Tkinter event loop
        self.root.mainloop()

    def run_countdown(self):
        room_info = self.wordy_game.getGameRoomInformation(self.room_name, self.username)
        countdown_value = int(room_info[2])

        if countdown_value > 0:
            self.countdown_label.config(text=str(room_info[2]))
            countdown_value -= 1
            self.root.after(1000, self.run_countdown)

        else:
            self.root.withdraw()
            GameGUI.GameGUI(self.wordy_game, self.username, self.room_name)
            # Destroy the rank GUI
            self.root.destroy()

    def close_window(self):
        self.wordy_game.logout(self.username)
        self.wordy_game.leaveGameRoom(self.username, self.room_name)
        self.root.destroy()

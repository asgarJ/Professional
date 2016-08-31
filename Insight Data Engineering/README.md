### Blackjack Game
============
I have implemented it in Python using the following five classes: Card, DealingShoe, Hand, Player, Dealer. The only input(optional) to the program is the number of decks you want to use within game, which is one by default. The program terminates either when player runs out of chips or when there is no card left in the machine. It prints the result of each round in every half second.

I believe that the code is well-commented and self-explanatory, and hope you’ll enjoy using it.

###### Card
--------------
Card class stores content of card and provides manipulations with cards such as checks if it's Ace, evaluates value of it and generates a 52-card deck.

Note that deciding the value of an Ace is not considered here, but inside Hand class, since the value of an Ace depends on other cards in hand.

###### DealingShoe
---------------------
DealingShoe class functions exactly as a card drawing machine. It can be initialised with a number (of decks) which is one by default.

###### Hand:
-----------------
Hand class stores all the information about cards in hand and provides manipulations such as: adding a card into hand, checking if hand is Blackjack and computing whole value in hand and so on.

Note that computation of hand value is done recursively, since there might be more than one Ace in hand. Furthermore, every Ace is considered either 1 or 11 once, which is, indeed, the requirement of game.
    
Hand class is also the parent class of both Dealer and Player classes, since they both have hands (of cards).

###### Player:
----------------
Player class holds the information about player's hand through inheriting Hand class and provides manipulations for deciding actions as well as the amount of bets. All of the operations are encapsulated within the class. In its constructor, it reads the two matrices of decisions based on possession of Ace from a text files which is available in this directory.

###### Dealer:
---------------------
Dealer class holds the information about dealer's hand through Hand class. It has also one extra attribute of face-up card which is set within a method where it also asserts that it’s the beginning of game.

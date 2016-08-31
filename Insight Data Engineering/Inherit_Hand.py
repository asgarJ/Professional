''' 
    Hand class stores all the information about cards in hand and provides manipulations such as:
    adding a card into hand, checking if hand is Blackjack and computing value in hand and so on.
    
    Note that computation of hand value is done recursively, since there might be more than one Ace in hand.
    Furthermore, every Ace is considered either 1 or 11 once, which is, indeed, the requirement of game.
    
    Hand class is also the parent class of both Dealer and Player classes, since they both have hands (of cards).

'''
class Hand(object):

    def __init__(self):
        self._value=0
        self._numOfAces=0
        self._hasAce = False
        self.hand = []

    def add_card(self, card):
        ''' Add a new card into the list of cards in hand.
        '''
        self.hand.append(card)

        if card.isAce():
            self._numOfAces += 1
            self._hasAce = True
        else :
            self._value += card.card_value()

    def value(self):
        ''' Compute the whole value of hand, recursively,
            '''
        if self._numOfAces > 0:
            if self._value < 4 or (6 < self._value and self._value < 11):
                self._value += 11
                self._numOfAces -= 1
            else :
                self._value += 1
                self._numOfAces -= 1
            return self.value()
        else :
            return self._value

    def isBlackjack(self):
        ''' Check if hand is BLACKJACK.
        '''
        for card in self.hand:
            if card.isAce():
                if self._value == 10:
                    return True
        return False

    def clear(self):
        ''' Clear the values in hand.
        '''
        self._value = 0
        self._numOfAces=0
        self._hasAce = False
        del self.hand[:]

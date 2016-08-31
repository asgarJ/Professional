import sys, time
from Tools import Card, DealingShoe
from Inherit_Hand import Hand


class Dealer(Hand):
    ''' This class holds the information about dealer's hand and inherits Hand class.
    '''

    def __init__(self):
        super(Dealer, self).__init__()

    def faceUpCard(self, card):
        ''' Dealer's face-up card.
        '''
        assert len(self.hand) == 0, "Face-up card must be only the first card!"     #   Assert that it's dealer's first card.
        self.up = card
        super(Dealer, self).add_card(card)

    def clear(self):
        ''' Clear face-up card, then override superclass method.
        '''
        self.up = None
        super(Dealer, self).clear()

class Player(Hand):
    ''' This class holds the information about player's hand and provides manipulations
        for deciding actions as well as the amount of bets. Also inherits Hand class.
    '''
    chips = 100;    bet = 0
    data = [];  dataAce = []

    def __init__(self):
        super(Player, self).__init__()

        with open('decisionMatrix.txt', 'r') as dec:
            dec.readline(); dec.readline()          # Skip first two lines, since they are for description.
            while True:
                line = dec.readline()
                if len(line) == 0:
                    break
                self.data.append(line.split()[1:])

        with open('decision_for_Ace.txt', 'r') as dec:
            dec.readline(); dec.readline()          # Skip first two lines, since they are for description.
            while True:
                line = dec.readline()
                if len(line) == 0:
                    break
                self.dataAce.append(line.split()[1:])


    def decide(self, dl_card):
        ''' Decide the action to take based on dealer's face-up card.
            Retrieve the decision from the matrix of decisions.
            '''
        if self._hasAce:
            if self._numOfAces == 2:
                self.value()
                return 'H'
            else :
                A = max(8-self._value, 0)
                B = 9 if dl_card.isAce() else dl_card.card_value()-2
                return self.dataAce[A][B]
        else :
            A = min(9, max(0, 17-self.value()))
            B = 9 if dl_card.isAce() else dl_card.card_value()-2
            return self.data[A][B]

    def make_bet(self):
        ''' Decide how much to bet.'''
        self.bet = min(8, self.chips)

    def Double(self, shoe):
        ''' Compute the amount you want to bet when you say "Double", given the amount of current bet.'''
        self.bet += min(self.bet/2, (self.chips - self.bet))    #   Bet half more. Check if chips < bet/2
        drawn_card = shoe.draw_a_card(self.chips)
        self.add_card(drawn_card)

    def Hit(self, shoe):
        ''' Action "Hit": Receive one more card.'''
        drawn_card = shoe.draw_a_card(self.chips)
        self.add_card(drawn_card)

    def won(self):
        ''' Increase chips by bet if player wins.'''
        self.chips += self.bet

    def lost(self):
        ''' Decrease chips by bet if player loses.'''
        self.chips -= self.bet

    def hasChips(self):
        ''' Check if player has chips.'''
        return (self.chips - self.bet > 0)

def main():

    N = raw_input("Enter the number of decks:\t")
    shoe = DealingShoe(N)
    player = Player()
    dealer = Dealer()
    
    print '\tGame Starts...'

    epoch = 1
    while player.chips > 0 :       #   keep on if you have chips.
#        print 'Player\'s Hand:', player.hand
#        print 'Dealer\'s Hand:', dealer.hand
        print 'Round %i  ' % epoch, ; epoch += 1
        time.sleep(.5)
        
        player.clear();     dealer.clear()            #   Clear all values except chips attribute of player.
        player.make_bet();  _chips = player.chips     #   Make a bet and store the amount of chips in variable _chips for better presentation of results.
            
        player.add_card(shoe.draw_a_card(_chips))     #   Player's first card.
        dealer.faceUpCard(shoe.draw_a_card(_chips))   #   Dealer's first i.e. face-up card.
        player.add_card(shoe.draw_a_card(_chips))     #   Player's second card.
        dealer.add_card(shoe.draw_a_card(_chips))     #   Dealer's second i.e. face-down card.

        ''' Check if BLACKJACK occurs at the beginning.'''
        if player.isBlackjack():
            if dealer.isBlackjack():
                print 'Draw!\tBoth got BLACKJACK!'
                continue
            else:
                player.won();   print 'Player won by BLACKJACK!'
                continue

        action = player.decide(dealer.up)               #   Decision is made here.
        while not action == 'S':
            if action == 'H':                           #   if Hit, then draw a new card.
                player.Hit(shoe)
            elif action == 'D':                         #   Take action Double if you have more chips, Hit otherwise.
                if player.hasChips():
                    player.Double(shoe);    print 'Player said "Double";',
                    break
                else:
                    player.Hit(shoe)
            action = player.decide(dealer.up)           #   Take action again after performing previous one.

        if player.value() > 21:                         #   If player busts, then loses chips as much as his/her bet.
            player.lost();  print 'Player busts, lost bet!'
            continue

        while dealer.value() < 17:                      #   Dealer hits until she has >=17 hand value.
            dealer.add_card(shoe.draw_a_card(_chips))

        if dealer.value() > 21:                         #   Check if dealer busts, then pays chips as much as player's bet.
            player.won();   print 'Dealer busts, Player won this bet!'
            continue


        if player.value() > dealer.value():             #   Compare hand values.
            player.won()
            print 'Player won this bet by "%i - %i"' % (player.value(), dealer.value())

        elif player.value() < dealer.value():
            player.lost()
            print 'Player lost this bet by "%i - %i"' % (player.value(), dealer.value())
        else :
            print 'Draw!'

        if player.chips <= 0:
            print '\nGAME OVER..\nPlayer runs out of chips.\n'


if __name__ == '__main__':
    main()

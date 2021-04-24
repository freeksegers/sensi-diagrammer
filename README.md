# sensi-diagrammer

Generates a diagram as shown below.

![diagram example](https://drive.google.com/uc?export=view&id=1yIUsBM1N7x97QSx4-iZw-ymXp3rBrNqO)

## Input

The program addmittedly has a very poor user interface. It reads input from the command line.

Three lists of words can be entered:

1. The list of words that will be rendered inside the diagram.
2. The list of words that will be rendered opposite on the outside of the diagram.
3. The list of words that will be rendered *between* the outer words.

For each list, the words are entered one per line. To indicate that the list is complete, on the last line a single period is entered.

### Sample input

```
> Woorden voor de binnenkant?
hot
mystery
mill
nerve
bond
pipe
nursery
crackpot
combination
indication
deviation
emergency
.

> Woorden voor de buitenkant?
trait
divorce
mechanism
jacket
paragraph
grain
exploration
implication
dry
evening
catch
prince
.

> Tussenwoorden voor de buitenkant?
entertain
fault
confusion
architect
elephant
horn
volume
pardon
brave
path
inquiry
need
.

```

The number of sections in the diagram is equal to the maximum length of the three lists of words. If not all lists are of equal length, some 'slots' will be left blank. Deliberate blanks can be created by entering empty lines in the word list.

The placement of the words is as follows. The first word in each list is placed at the "3 o'clock" section. Consecutive words are place clockwise.

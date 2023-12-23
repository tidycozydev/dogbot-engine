# Dogbot Engine
Beginning of what should become the engine for my talking bot, Dogbot, from my Vaadin escape game.

Right now, just very basic code to test OpenNLP and try to figure it out.

## Libraries
- Spring Boot
- Apache OpenNLP

## Models sources
- https://opennlp.sourceforge.net/models-1.5/
- https://opennlp.apache.org/models.html
- https://raw.githubusercontent.com/richardwilly98/elasticsearch-opennlp-auto-tagging/master/src/main/resources/models/en-lemmatizer.dict

I did a small modification on the former one: 

It looks like the POS tag NNN was standing for an exact match with the lemma in the dictionary. Problem was that NNN was 
not a tag returned by the POS tagger from Apache OpenNLP, it was only returning NN. So I replaced NNN in the dictionnary 
by NN.

## Useful links
- https://universaldependencies.org/u/pos/
- https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
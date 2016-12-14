/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uma.m2align.strike;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author cristian
 */
public class Strike {

  private ArrayList<Contact> contacts;

  double[][] cs_mat;
  int[][] blosum62;

  public Strike(String _PDBPath, List<StringBuilder> listOfSequenceNames) {
    contacts = getContacts(_PDBPath, listOfSequenceNames);
    cs_mat = cs();
    blosum62 = blosum62();
  }

  private  ArrayList<Contact> getContacts(String PDBPath,List<StringBuilder> listOfSequenceNames){

    ArrayList<Contact> Contacts = new ArrayList<Contact>();
    Contact contact;

    for ( StringBuilder SeqName : listOfSequenceNames ) {
        File contactFile = new File(PDBPath + "/" + SeqName + ".contacts");
        
        if(contactFile.exists()){
              contact = new Contact('#');
              contact.read_contact_file(PDBPath + "/" + SeqName + ".contacts", 5);
              Contacts.add(contact);
        }
    }
      

    return Contacts;
  }
  
  
  public double compute(char[][] _sequences, List<StringBuilder> SequencesNames, boolean Normalized) {

    int k;
    double avg = 0;
    int tmp_num = contacts.size();
    double x;
    for (k = 0; k < tmp_num; ++k) {
      x = score_cs(contacts.get(k), 0, Normalized, _sequences, SequencesNames);
      avg += x;
    }

    return avg / tmp_num;
  }

  private int[][] blosum62() {

    int[][] _blosum62 =
            {
                    {4, -2, 0, -2, -1, -2, 0, -2, -1, -1, -1, -1, -1, -2, -4, -1, -1, -1, 1, 0, -4, 0, -3, -1, -2, -1},
                    {-2, 4, -3, 4, 1, -3, -1, 0, -3, -3, 0, -4, -3, 4, -4, -2, 0, -1, 0, -1, -4, -3, -4, -1, -3, 0},
                    {0, -3, 9, -3, -4, -2, -3, -3, -1, -1, -3, -1, -1, -3, -4, -3, -3, -3, -1, -1, -4, -1, -2, -1, -2, -3},
                    {-2, 4, -3, 6, 2, -3, -1, -1, -3, -3, -1, -4, -3, 1, -4, -1, 0, -2, 0, -1, -4, -3, -4, -1, -3, 1},
                    {-1, 1, -4, 2, 5, -3, -2, 0, -3, -3, 1, -3, -2, 0, -4, -1, 2, 0, 0, -1, -4, -2, -3, -1, -2, 4},
                    {-2, -3, -2, -3, -3, 6, -3, -1, 0, 0, -3, 0, 0, -3, -4, -4, -3, -3, -2, -2, -4, -1, 1, -1, 3, -3},
                    {0, -1, -3, -1, -2, -3, 6, -2, -4, -4, -2, -4, -3, 0, -4, -2, -2, -2, 0, -2, -4, -3, -2, -1, -3, -2},
                    {-2, 0, -3, -1, 0, -1, -2, 8, -3, -3, -1, -3, -2, 1, -4, -2, 0, 0, -1, -2, -4, -3, -2, -1, 2, 0},
                    {-1, -3, -1, -3, -3, 0, -4, -3, 4, 3, -3, 2, 1, -3, -4, -3, -3, -3, -2, -1, -4, 3, -3, -1, -1, -3},
                    {-1, -3, -1, -3, -3, 0, -4, -3, 3, 3, -3, 3, 2, -3, -4, -3, -2, -2, -2, -1, -4, 2, -2, -1, -1, -3},
                    {-1, 0, -3, -1, 1, -3, -2, -1, -3, -3, 5, -2, -1, 0, -4, -1, 1, 2, 0, -1, -4, -2, -3, -1, -2, 1},
                    {-1, -4, -1, -4, -3, 0, -4, -3, 2, 3, -2, 4, 2, -3, -4, -3, -2, -2, -2, -1, -4, 1, -2, -1, -1, -3},
                    {-1, -3, -1, -3, -2, 0, -3, -2, 1, 2, -1, 2, 5, -2, -4, -2, 0, -1, -1, -1, -4, 1, -1, -1, -1, -1},
                    {-2, 4, -3, 1, 0, -3, 0, 1, -3, -3, 0, -3, -2, 6, -4, -2, 0, 0, 1, 0, -4, -3, -4, -1, -2, 0},
                    {-4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4},
                    {-1, -2, -3, -1, -1, -4, -2, -2, -3, -3, -1, -3, -2, -2, -4, 7, -1, -2, -1, -1, -4, -2, -4, -1, -3, -1},
                    {-1, 0, -3, 0, 2, -3, -2, 0, -3, -2, 1, -2, 0, 0, -4, -1, 5, 1, 0, -1, -4, -2, -2, -1, -1, 4},
                    {-1, -1, -3, -2, 0, -3, -2, 0, -3, -2, 2, -2, -1, 0, -4, -2, 1, 5, -1, -1, -4, -3, -3, -1, -2, 0},
                    {1, 0, -1, 0, 0, -2, 0, -1, -2, -2, 0, -2, -1, 1, -4, -1, 0, -1, 4, 1, -4, -2, -3, -1, -2, 0},
                    {0, -1, -1, -1, -1, -2, -2, -2, -1, -1, -1, -1, -1, 0, -4, -1, -1, -1, 1, 5, -4, 0, -2, -1, -2, -1},
                    {-4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4},
                    {0, -3, -1, -3, -2, -1, -3, -3, 3, 2, -2, 1, 1, -3, -4, -2, -2, -3, -2, 0, -4, 4, -3, -1, -1, -2},
                    {-3, -4, -2, -4, -3, 1, -2, -2, -3, -2, -3, -2, -1, -4, -4, -4, -2, -3, -3, -2, -4, -3, 11, -1, 2, -2},
                    {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -4, -1, -1, -1, -1, -1, -4, -1, -1, -1, -1, -1},
                    {-2, -3, -2, -3, -2, 3, -3, 2, -1, -1, -2, -1, -1, -2, -4, -3, -1, -2, -2, -2, -4, -1, 2, -1, 7, -2},
                    {-1, 0, -3, 1, 4, -3, -2, 0, -3, -3, 1, -3, -1, 0, -4, -1, 4, 0, 0, -1, -4, -2, -2, -1, -2, 4}
            };


    int j;
    int[][] blosum62_mat = new int[26][];
    for (int i = 0; i < 26; ++i) {
      blosum62_mat[i] = new int[26];
      for (j = 0; j < 26; ++j) {
        blosum62_mat[i][j] = _blosum62[i][j];
      }
    }

    return blosum62_mat;
  }

  private double[][] cs() {
    double[][] _cs_mat =
            {
                    {-0.744055875823878, 0, -0.302714763884203, -8.18065408358324, -9.43250108594488, 4.0024319077819, 0, -3.44290454108591, 3.85786863942911, 0, -7.48891663653909, 2.85310469061013, 1.44275947475201, -6.88728722453311, 0, -4.60033702306021, -5.07604456146783, -4.07450237434039, -6.01162469424305, -3.79684159474661, 0, 3.68503771993837, 3.77629309202085, 0, 2.30547732746785, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0.563429964483508, 0, 23.216631475072, -2.46542154563067, -6.72154030935699, 8.8817354132774, 0, 3.20305900859143, 6.45432849214352, 0, -2.50078977078133, 6.00125028548607, 6.25869831131468, 0.378437968143603, 0, 2.11768974544946, -1.70881749866372, 1.18009003408374, 0.334810553015332, 0.225137748719709, 0, 7.82014196956194, 9.577338158519, 0, 7.06811548677035, 0},
                    {-8.35163406201817, 0, -2.18731267859346, -0.218002408932713, -8.83887440995716, -0.686016175955611, 0, 2.17660681512101, -3.79113118180437, 0, 1.24650083684101, -4.94895524022016, -4.55988908963644, -1.43633017659302, 0, -5.01220415079131, -4.41848441506915, 5.19967117328199, -4.08551959944597, -3.41215924689882, 0, -2.41681947053046, 3.31679259913042, 0, 2.13215246195462, 0},
                    {-9.950903462798, 0, -6.97835976568042, -9.40741613874478, -6.66818887034316, -2.69010742926591, 0, -0.687100811252813, -4.88018555189452, 0, 0.292533478461261, -5.24591068348682, -5.17566558598087, -5.80533065701881, 0, -5.93230821405525, -5.77566569026334, 3.26563880412349, -6.87847328766169, -5.5200043098517, 0, -5.43781499307001, 1.84918821122744, 0, 0.279569067093439, 0},
                    {3.67725126351843, 0, 8.7710002220826, -0.722240808041252, -2.70505671128115, 13.9923883824675, 0, 4.37388335008561, 11.2292913417867, 0, -0.52331140390248, 10.96968518683, 9.93735965253691, 0.0458151874604124, 0, 2.38227895696109, 1.32385179829162, 3.82598885660533, 0.208057460461618, 2.67049207079983, 0, 10.5250957740446, 12.0412570276562, 0, 10.0146608775621, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {-3.10216149835793, 0, 3.82529448538273, 2.16612463386772, 0.052029486982624, 4.21102831437299, 0, 9.49792354772278, 1.50771884705347, 0, -3.02740642811791, 1.63224047570807, 3.74464198050375, 0.267892937193302, 0, -0.0299429064730965, 0.269787234259044, 2.44541786827413, -0.293416316686081, 0.786792285904228, 0, 1.02531084556973, 7.63235746651112, 0, 6.29901500153958, 0},
                    {3.74349355722008, 0, 6.25610916712523, -3.60785220179623, -4.79560653063696, 11.5185741110095, 0, 1.55950262214948, 12.9089012970287, 0, -2.95219382759794, 10.9023524846628, 8.32655707815868, -2.59234288581899, 0, -0.218850768824713, -0.803227535434448, 0.322674298350308, -1.86999228038304, 1.85308807950936, 0, 10.7707377068528, 9.65650468156088, 0, 8.53229371552018, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {-7.68623245516504, 0, -4.23985336946803, 1.1443213113889, 0.723436662981594, -0.199066191408116, 0, -3.29238310043893, -2.99256826306329, 0, -0.105272837432747, -3.25622060503402, -3.83965951581705, -2.47727008479023, 0, -4.11844544680173, -3.97775804984858, -2.45558345362291, -5.05503682868254, -2.7681567364425, 0, -1.6618422965417, 1.79130594754075, 0, 1.85937593327484, 0},
                    {2.70234583828839, 0, 5.82777438871162, -5.00631677417541, -5.13794578445595, 11.3210694876418, 0, 1.23953643741971, 11.018661110017, 0, -3.37810839296511, 11.9418737408998, 7.59430180722096, -3.27745738962111, 0, -0.178941058608256, -1.48112963358673, 0.904958024861746, -2.74428197881832, 1.14236626008265, 0, 10.2043435797671, 10.1601656618937, 0, 7.67592505167412, 0},
                    {1.55938642840778, 0, 6.29019440034364, -4.04763002291339, -3.78753190045664, 10.5636744337802, 0, 2.62760208470374, 8.77182329713585, 0, -3.15186915228681, 7.84254150450822, 11.711539124619, -1.66160274002826, 0, 0.616594251751747, -0.484055295747724, 0.914603692262962, -1.95969917087105, 0.758184360747387, 0, 6.89809969641056, 10.1468295187994, 0, 7.67787773603446, 0},
                    {-6.97360408439622, 0, 0.536794404756985, -1.64213332435025, -5.84176056980382, 0.492930553149589, 0, 0.421102324936293, -2.52766425574413, 0, -2.28331611375334, -3.33373215827839, -1.49458424384894, 3.54042853933437, 0, -2.24045763023172, -2.32632019925434, 0.47610050074255, -3.61872525748528, -2.05790633052002, 0, -2.53122932744671, 3.5154572067431, 0, 2.17461479980675, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {-4.02589696574941, 0, 1.8004002990208, -4.4451438758483, -5.59156841071773, 2.71835194380965, 0, 0.530800872441051, 0.17981113130917, 0, -3.94978191425315, 0.349175798604766, 1.14329322630196, -1.74901312668558, 0, 1.88011666965195, -2.31939347590581, 0.665304173903774, -4.68774787500018, -1.24691703239316, 0, 0.647276956486846, 6.67939336538368, 0, 5.81506379335207, 0},
                    {-5.43452385328595, 0, -2.01187099361701, -5.18273328165406, -5.6978190434846, 1.19759088657582, 0, -0.0759779585919578, -0.836130032802324, 0, -3.97135804963012, -1.22326002187265, -0.787220842424092, -2.30419629642493, 0, -2.73547549822475, 2.38902737633693, 0.569428466580743, -3.81699410796334, -1.51550803986253, 0, -1.39939693070244, 4.70924146078409, 0, 2.5523347016674, 0},
                    {-4.29460695368328, 0, 0.486680636965371, 4.97864298162259, 3.73792347220258, 4.120405016859, 0, 2.07793813286723, 0.484656930029987, 0, -2.14886974285032, 0.918037330907308, -0.139001464315301, -0.0719262273602284, 0, 0.255314473807657, 0.721341602266994, 4.56708300380649, -1.89726669816384, -0.302663528339019, 0, 0.811421867579365, 6.98019169754308, 0, 4.13430458624569, 0},
                    {-5.97434915125163, 0, 0.37000120516729, -3.84227496633226, -6.2868731804832, 0.916830441207946, 0, -0.245650958044545, -1.61275897569608, 0, -4.52756656046882, -2.34350770176258, -1.14582876794365, -3.06224276480934, 0, -4.54673183595703, -3.29087890173511, -1.18020888354511, -1.26690806529519, -2.32693632188832, 0, -1.3342050295879, 3.38185822875439, 0, 1.31315196123521, 0},
                    {-3.11731389780309, 0, 0.777086537192059, -3.61720275969269, -5.634874749723, 3.44217523582176, 0, 0.773750223409985, 2.51516697324763, 0, -2.31731371405645, 1.77235516403156, 1.22299484344795, -1.48552401197085, 0, -0.822581048247189, -0.939216911496162, 0.287544821379587, -2.26940290177121, 1.98280130317421, 0, 2.37061530829648, 4.64228021321104, 0, 3.39402873869243, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {3.47525983750936, 0, 7.67451376582157, -2.43297021428344, -5.56834852431571, 10.9380813232173, 0, 0.977222270090444, 10.6672101743659, 0, -1.6592912761197, 10.0381020190232, 6.52573994231515, -3.03191596981711, 0, 0.325204390252507, -1.70803866377506, 0.568752061815176, -1.82358388525935, 1.8427638669967, 0, 12.2107146432932, 8.7060924695392, 0, 8.01050059154028, 0},
                    {2.65030580535629, 0, 9.89656271080995, 2.75539108474302, 1.10985791384256, 11.850117069215, 0, 6.63670372488712, 9.77104878522572, 0, 1.49073687321265, 9.87838725652592, 9.76942623897098, 3.07444085903699, 0, 5.46732812430464, 4.42312207819857, 6.86845869156183, 1.82444154072241, 3.58113522413606, 0, 8.75438014272746, 16.5887015359961, 0, 10.6389300440303, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {2.1101256430652, 0, 6.39719325710416, 2.01253588448169, 0.126949825672711, 10.1489062237833, 0, 5.55441616678257, 8.07070277474075, 0, 1.46586436884238, 7.13299422800749, 7.20287205512148, 1.60724349375672, 0, 4.9235805791322, 2.32699599640634, 4.17964145146598, 0.513201790334874, 1.75557819027692, 0, 7.53688784559063, 10.8105318880362, 0, 10.3916984190201, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            };

    int j;
    double[][] cs_mat = new double[26][];
    for (int i = 0; i < 26; ++i) {
      cs_mat[i] = new double[26];
      for (j = 0; j < 26; ++j) {
        cs_mat[i][j] = _cs_mat[i][j];
      }
    }

    return cs_mat;
  }


  private double score_cs(Contact contact, int min_dist, boolean normalize, char[][] _sequences, List<StringBuilder> _names) {
    int _num_sequences = _sequences.length;
    String seq_name = contact.get_seq_name();
    int template_seq = 0;

    for (template_seq = 0; template_seq < _names.size() - 1; template_seq++) {
      if (_names.get(template_seq).toString().equals(seq_name))
        break;
    }

    if ((template_seq == _names.size()) || (template_seq > _num_sequences)) {
      System.out.println("ERROR: Sequence " + seq_name + " not found in alignment.");
      System.exit(2);
    }

    NumContacts num_contacts = new NumContacts();
    int[][] contact_pairs = _map_contacts(_sequences[template_seq], contact, num_contacts);


//            System.out.printf("template_seq %d _sequences %s\nName %s\n",template_seq, _sequences.get(template_seq).toString(), _names.get(template_seq).toString());
//            for(int k=0;k<num_contacts;k++){
//            
//                for(int j=0;j<contact_pairs[k].length;j++){
//                    
//                    System.out.print(contact_pairs[k][j] + " ");
//                    
//                }
//                
//                System.out.print("- ");
//            }
//            System.out.println("");


    if (num_contacts.Num < 50) {
//                    System.out.printf("**************   !WARNING!   **************\n");
//                    System.out.printf("      Only few contacts avilable!\n");
//                    System.out.printf("    Scoring might be very inaccurate\n");
//                    System.out.printf("   # contacts: %d\n", num_contacts);
//                    System.out.printf("   PDB: %s   -   Chain: %c\n", contact.pdb_name(), contact.chain_name());
//                    System.out.printf("**************   !WARNING!   **************\n");
    }


    char char1;
    char char2;
    int i;
    double normalize_value = 0;

    int contact_counter = 0;
    if (normalize) {
      for (i = 0; i < num_contacts.Num; ++i) {
        if (contact_pairs[i][1] - contact_pairs[i][0] >= min_dist) {
          char1 = _sequences[template_seq][contact_pairs[i][0]];
          char2 = _sequences[template_seq][contact_pairs[i][1]];
          if ((char1 != 'X') && (char2 != 'X') && (char1 != 'B') && (char2 != 'B') && (char1 != 'Z') && (char2 != 'Z') && (char1 != 'J') && (char2 != 'J')) {
            ++contact_counter;
            normalize_value += cs_mat[char1 - 65][char2 - 65];
          }
        }
      }
      normalize_value /= contact_counter;
    }

    int j;
    double cs_score = 0;

    contact_counter = 0;
    for (i = 0; i < _num_sequences; ++i) {
      if (i != template_seq) {

        for (j = 0; j < num_contacts.Num; ++j) {
          if (contact_pairs[j][1] - contact_pairs[j][0] >= min_dist) {
            char1 = _sequences[i][contact_pairs[j][0]];
            char2 = _sequences[i][contact_pairs[j][1]];

            if ((char1 != 'X') && (char2 != 'X') && (char1 != 'B') && (char2 != 'B') && (char1 != 'Z') && (char2 != 'Z') && (char1 != 'J') && (char2 != 'J')) {
              ++contact_counter;
              if ((char1 != '-') && (char2 != '-')) {
                cs_score += cs_mat[char1 - 65][char2 - 65];
              }
            }
          }
        }
      }
    }


    if(cs_score>0){
        if (normalize) {
            return (cs_score / contact_counter) / normalize_value;
        } else {
            return cs_score / contact_counter;
        }
    }else{
        return 0;
    }
  }

//  private ArrayList<Contact> getContacts(String PDBPath, String ConecctionFile) {
//
//    ArrayList<Contact> Contacts = new ArrayList<Contact>();
//
//    FileReader fr = null;
//    BufferedReader br = null;
//
//    try {
//      fr = new FileReader(PDBPath + ConecctionFile);
//      br = new BufferedReader(fr);
//
//      String linea;
//      Contact contact;
//
//      while ((linea = br.readLine()) != null) {
//
//        String[] parts = linea.split(" ");
//
//        contact = new Contact(PDBPath + parts[0] + ".pdb", '#');
//        contact.read_contact_file(PDBPath + parts[0] + ".contacts", 5);
//        Contacts.add(contact);
//
//      }
//
//    } catch (Exception e) {
//      e.printStackTrace();
//
//    } finally {
//
//      try {
//        if (null != fr) {
//          fr.close();
//        }
//      } catch (Exception e2) {
//        e2.printStackTrace();
//      }
//
//    }
//    return Contacts;
//  }


  private TreeMap<Integer, Integer> _gotoh_align(String seq1, String seq2) {

    int len1 = seq1.length() + 1;
    int len2 = seq2.length() + 1;
    int i, j;

    int[][] matrix_m = new int[len1][];
    int[][] matrix_h = new int[len1][];
    int[][] matrix_v = new int[len1][];
    for (i = 0; i < len1; ++i) {
      matrix_m[i] = new int[len2];
      matrix_h[i] = new int[len2];
      matrix_v[i] = new int[len2];
    }


    //  set scoring values
    int gop = -11;
    int gep = -1;
    int tmp_value;


    //  calculate Matrix
    matrix_m[0][0] = 0;
    matrix_v[0][0] = Integer.MIN_VALUE;
    matrix_h[0][0] = Integer.MIN_VALUE;

    for (i = 1; i < len1; i++) {
      matrix_v[i][0] = matrix_m[0][0] + gop + i * gep;
      matrix_m[i][0] = matrix_v[i][0];
      matrix_h[i][0] = Integer.MIN_VALUE;
    }

    for (j = 1; j < len2; j++) {
      matrix_h[0][j] = matrix_m[0][0] + gop + j * gep;
      matrix_m[0][j] = matrix_h[0][j];
      matrix_v[0][j] = Integer.MIN_VALUE;
      for (i = 1; i < len1; i++) {
        matrix_h[i][j] = Math.max(matrix_m[i][j - 1] + gop, matrix_h[i][j - 1]) + gep;
        matrix_v[i][j] = Math.max(matrix_m[i - 1][j] + gop, matrix_v[i - 1][j]) + gep;
        tmp_value = Math.max(matrix_h[i][j], matrix_v[i][j]);
        matrix_m[i][j] = Math.max(tmp_value, matrix_m[i - 1][j - 1] + blosum62[seq1.charAt(i - 1) - 65][seq2.charAt(j - 1) - 65]);
      }
    }
//    for (int z = 0; z < 26; ++z)
//    {
//      blosum62[z] = null;
//    }
//    blosum62 = null;

    --i;
    --j;

    TreeMap<Integer, Integer> mapping = new TreeMap<Integer, Integer>();
    char mode = 'm';
    if (matrix_h[i][j] > matrix_m[i][j]) {
      mode = 'h';
    }
    if (matrix_v[i][j] > matrix_m[i][j]) {
      mode = 'v';
    }

    while ((i > 0) && (j > 0)) {
      if (mode == 'v') {
        if (matrix_v[i][j] != matrix_v[i - 1][j] + gep) {
          mode = 'm';
        }
        --i;
      } else if (mode == 'h') {
        if (matrix_h[i][j] != matrix_h[i][j - 1] + gep) {
          mode = 'm';
        }
        --j;
      } else if (mode == 'm') {
        if (matrix_h[i][j] == matrix_m[i][j]) {
          mode = 'h';
          continue;
        }
        if (matrix_v[i][j] == matrix_m[i][j]) {
          mode = 'v';
          continue;
        }
        --j;
        --i;
        mapping.put(i, j);
      }
    }


    //  free memory
    for (i = 0; i < len1; ++i) {
      matrix_m[i] = null;
      matrix_h[i] = null;
      matrix_v[i] = null;
    }
    matrix_m = null;
    matrix_h = null;
    matrix_v = null;


    return mapping;
  }

  private int[][] _map_contacts(char[] aln_string, Contact contact, NumContacts num_contacts) {

    int aln_length = aln_string.length;
    String gapless_temp = "";
    int[] seq2aln = new int[aln_length];
    int i;

    int j = -1;
    for (i = 0; i < aln_length; ++i) {
      if (aln_string[i] != '-') {
        gapless_temp += aln_string[i];
        seq2aln[++j] = i;
      }
    }


    TreeMap<Integer, Integer> mapping = _gotoh_align(contact.get_sequence(), gapless_temp);

    num_contacts.Num=contact.num_contacts() ;

    int[][] contact_pairs = new int[contact.num_contacts()][2];

    ArrayList<ArrayList<Integer>> contacts = contact.get_contacts();

    int tmp_length;
    int seq_length = contact.get_seq_length();

    int mapped_i;

    num_contacts.Num = 0;
    for (i = 0; i < seq_length; ++i) {
      if (!mapping.containsKey(i))
        continue;
      mapped_i = seq2aln[mapping.get(i)];
      tmp_length = contacts.get(i).size();
      for (j = 0; j < tmp_length; ++j) {
        if (!mapping.containsKey(contacts.get(i).get(j)))
          continue;
        contact_pairs[num_contacts.Num][0] = mapped_i;
        contact_pairs[num_contacts.Num][1] = seq2aln[mapping.get(contacts.get(i).get(j))];
        ++num_contacts.Num;
      }
    }

    seq2aln = null;

    return contact_pairs;
  }

}

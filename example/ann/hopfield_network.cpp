#include <aja/ann/neuron.hpp>
#include <aja/ann/network.hpp>

using namespace std;

int main (int argc, char *argv[])
{
    //weights between neuron 1 and 1, 2, 3, 4
    int wt1n[]= {0,-3,3,-3};
    //weights between neuron 2 and 1, 2, 3, 4
    int wt2n[]= {-3,0,-3,3};
    //weights between neuron 3 and 1, 2, 3, 4
    int wt3n[]= {3,-3,0,-3};
    //weights between neuron 4 and 1, 2, 3, 4
    int wt4n[]= {-3,3,-3,0};

    int patrn1[]= {1,0,1,0};
    int i;

    cout<<"\nThis program is for a Hopfield Network with a single layer of";
    cout<<"\n4 fully interconnected neurons. The network should recall the";
    cout<<"\npatterns 1010 and 0101 correctly.\n";

    //create the network by calling its constructor.
    // the constructor calls neuron constructor as many times as the number of
    // neurons in the network.
    network hf_net(wt1n,wt2n,wt3n,wt4n);

    cout << "========================================\n\n";
    //present a pattern to the network and get the activations of the neurons
    hf_net.activation(patrn1);
    cout << "========================================\n\n";

    //check if the pattern given is correctly recalled and give message
    for(i=0;i<4;i++)
    {
        if (hf_net.output[i] == patrn1[i])
        {
            cout << " pattern= " << patrn1[i]
                 << " output = " << hf_net.output[i]
                 << " component matches!\n";
        }
        else
        {
            cout << " pattern= " << patrn1[i]
                 << " output = " << hf_net.output[i]
                 << " discrepancy occurred!\n";
        }
    }
    cout << "========================================\n\n";

    int patrn2[]= {0,1,0,1};
    hf_net.activation(patrn2);
    cout << "========================================\n\n";

    for(i=0; i<4; i++)
    {
        if(hf_net.output[i] == patrn2[i])
        {
            cout << " pattern = " << patrn2[i]
                 << " output = " << hf_net.output[i]
                 <<" component matches!\n";
        }
        else
        {
            cout << " pattern= " << patrn2[i]
                 << " output = " << hf_net.output[i]
                 << " discrepancy occurred!\n";
        }
    }
}

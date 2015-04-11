//Copyright (c) 2015, Mageswaran.D<mageswaran1989@gmail.com>
//All rights reserved.

//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//1. Redistributions of source code must retain the above copyright
//   notice, this list of conditions and the following disclaimer.
//2. Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
//3. All advertising materials mentioning features or use of this software
//   must display the following acknowledgement:
//   This product includes software developed by the <organization>.
//4. Neither the name of the <organization> nor the
//   names of its contributors may be used to endorse or promote products
//   derived from this software without specific prior written permission.

//THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
//EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
//DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#include <vector>
#ifndef _AJA_PROCESSING_PERCEPTRON_HPP_
#define _AJA_PROCESSING_PERCEPTRON_HPP_

#include <vector>
#include <aja/ann/activation_function.hpp>
namespace aja
{
  /** \brief Class that represents a single neuron
     * A single neuron shall have
     * weight
     * input
     * activation function
     * thersold value
     * output
     * \author Mageswaran
     * \ingroup Processing
     */
  class Perceptron
  {
      Perceptron(const Perceptron& other);

      explicit Perceptron(void);
      explicit Perceptron(unsigned int num_inputs);

      ~Perceptron();

      Perceptron operator = (const Perceptron&);
      bool operator == (const Perceptron&) const;

      void set(void);
    protected:
    private:
      double bias;
      unsigned int num_inputs;
      unsigned int num_output;
      std::vector<double> synaptic_weights;
      bool debug_msgs;
      activation_functions activation_function;
  };
}
#endif //_AJA_PROCESSING_PERCEPTRON_HPP_

package doodle
package chart

package object syntax extends AsPointSyntax
    with AsSeriesSyntax
    with ChartSyntax
{
  object asPoint extends AsPointSyntax
  object asSeries extends AsSeriesSyntax
  object chart extends ChartSyntax
}
